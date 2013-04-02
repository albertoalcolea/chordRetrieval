package es.tony.crawling.chordsCrawler;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import es.tony.crawling.dao.ChordDao;

public class MyCrawler extends WebCrawler {

	// private final static Logger log = Logger.getLogger(MyCrawler.class);

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

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
				&& href.startsWith("*****");
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println(url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();

			Pattern p_chords = Pattern.compile(".*?<u>(.*?)</u>.*?", Pattern.DOTALL);
			Pattern p_data = Pattern
					.compile(
							".*?strKey = \"(.*?)\".*?artist = \"(.*?)\".*?title = \"(.*?)\".*?",
							Pattern.DOTALL);

			Matcher m_chords = p_chords.matcher(html);
			Matcher m_data = p_data.matcher(html);

			JSONObject songData = new JSONObject();
			JSONArray chords = new JSONArray();
			
			if (m_data.matches()) {
				songData.put("key", m_data.group(1));
				songData.put("group", m_data.group(2));
				songData.put("song", m_data.group(3));
				
				while (m_chords.find()) {
					chords.add(m_chords.group(1));
				}
				
				songData.put("chords", chords);
				System.out.println(songData);
				ChordDao.insertar(songData.toString());
			} else {
				System.out.println("No matches");
			}
		}
	}
}
