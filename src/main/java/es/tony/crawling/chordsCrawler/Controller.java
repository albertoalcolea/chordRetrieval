package es.tony.crawling.chordsCrawler;


import org.jongo.Jongo;



import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import es.tony.crawling.dao.ChordDao;

public class Controller {

	//private final static Logger log = Logger.getLogger(Controller.class);

	public static void main(String[] args) throws Exception {
		ChordDao.inicializar();
		
		String crawlStorageFolder = "data";
		int numberOfCrawlers = 8; //1

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(2000);
		config.setMaxDepthOfCrawling(-1); //2
		config.setMaxPagesToFetch(-1);  //1000

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher,
				robotstxtServer);

		// Digits
		for (char c='0'; c<='9'; c++)
			controller.addSeed("*****/browse/" + c);
		// Alphabetics
		for (char c='a'; c<='z'; c++)
			controller.addSeed("*****/browse/" + c);
		

		controller.start(MyCrawler.class, numberOfCrawlers);
	}
}
