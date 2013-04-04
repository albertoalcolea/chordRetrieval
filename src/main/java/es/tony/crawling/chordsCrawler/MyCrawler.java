package es.tony.crawling.chordsCrawler;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import es.tony.crawling.dao.ChordDao;

public class MyCrawler extends WebCrawler {

	private final static Logger log = Logger.getLogger(MyCrawler.class);

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz|htm|asp)"
					+ "|/key-[0-9][0-1]?|hits|latest|keyboard|easy-version"
					+ "|(?:tabs|bass|riff|drums|harmonics|flute|cavaco"
					+ "|videos|style|profile|site|rss)(?:/.+)?"
					+ "|(?:tutorial|videolessons).htm\\?.+)$");
//					+ "|login.htm|signup-free.htm|genres.htm"
//					+ "|requestvideo.htm|tutorials.htm|chord-dictionary.htm"
//					+ "|riffs-list.htm|scales.htm|upgrade-premium.htm"
//					+ "|backing-tracks.htm|submit-tutorial.asp"
//					+ "|chords-editor.htm|chords-explorer.htm"
//					+ "|guitar-chord-library.htm|bonus.htm)$");

	private static File storageFolder;
	private static String[] crawlDomains;

	public static void configure(String[] domain, String storageFolderName) {
		MyCrawler.crawlDomains = domain;

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://www.e-chords.com/chords");
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		log.info(url);
		
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			
			// If title isn't "song chords by group" we don't do the analysis 
			Pattern p_title = Pattern.compile("(.+) chords by (.+)");
			Matcher m_title = p_title.matcher(htmlParseData.getTitle());
			
			if (m_title.matches()) {
				JSONObject songData = new JSONObject();
				JSONArray chords = new JSONArray();
				
				songData.put("group", m_title.group(2));
				songData.put("song", m_title.group(1));
				
				String html = htmlParseData.getHtml();
				
				Pattern p_data = Pattern
						.compile(".*?strKey = \"(.+?)\".*?<pre.*?>(.+?)</pre>.*?", 
								Pattern.DOTALL);

				Matcher m_data = p_data.matcher(html);
				
				if (m_data.matches()) {
					songData.put("key", m_data.group(1));
					
					// Only looking for into <pre> </pre>
					Pattern p_chords = Pattern.compile("<u>(.+?)</u>", Pattern.DOTALL);
					Matcher m_chords = p_chords.matcher(m_data.group(2));
					
					while (m_chords.find()) {
						chords.add(m_chords.group(1));
					}
					
					songData.put("chords", chords);
					ChordDao.insertar(songData.toString());
					log.info("Added: " + songData.get("song") 
						   + " by " + songData.get("group"));
				}
			}
		}
	}
}
