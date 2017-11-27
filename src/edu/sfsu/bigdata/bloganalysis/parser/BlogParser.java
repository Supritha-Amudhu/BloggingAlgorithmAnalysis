package edu.sfsu.bigdata.bloganalysis.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.sfsu.bigdata.bloganalysis.model.Link;
import edu.sfsu.bigdata.bloganalysis.util.StringUtils;

public class BlogParser {

	private String blogURL;
	private static final String GET = "GET";
	private String blogHTML;
	private List<Link> outlinks;
	private Document htmlDocument;
	private long commentsCount;
	
	public BlogParser(String blogURL) {
		this.blogURL = blogURL;
		this.outlinks = new ArrayList<>();
		this.initialize();
		this.parseLinks();
		this.parseComments();
	}
	
	private void initialize() {
		try {
			URL url = new URL(blogURL);
			System.setProperty("http.agent", "Chrome");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(GET);
			StringBuilder blogMarkup = new StringBuilder();
			
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
		      String line;
		      while ((line = bufferedReader.readLine()) != null) {
		         blogMarkup.append(line);
		      }
		      bufferedReader.close();
		      this.blogHTML = blogMarkup.toString();
		      this.htmlDocument = Jsoup.parse(this.blogHTML);
		} catch (MalformedURLException e) {
			System.out.println("Error creating URL.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error opening connection. ");
			e.printStackTrace();
		}
	}
	
	private boolean isValidHTMLDocument() {
		return this.htmlDocument != null && this.htmlDocument.body() != null;
	}
	
	private void parseLinks() {
		if (isValidHTMLDocument()) {
			Element body = this.htmlDocument.body();
			Elements links = body.select("a[href]");
			Iterator<Element> linksIterator = links.iterator();
			while(linksIterator.hasNext()) {
				Element link = linksIterator.next();
				Link outlink = new Link();
				outlink.setLinkText(link.text());
				String outlinkURL = link.attr("href");
				if(StringUtils.isNotEmpty(outlinkURL)) {
					outlink.setUrl(outlinkURL);
				}
				this.outlinks.add(outlink);
			}
		}
	}
	
	private void parseComments() {
		if (isValidHTMLDocument()) {
			Element body = this.htmlDocument.body();
			Element actionFooter = body.selectFirst(".js-postActionsFooter");
			Elements commentsIconBlock = actionFooter
					.select("button[data-action=scroll-to-responses]");
			Iterator<Element> commensIconBlockIterator = commentsIconBlock.iterator();
			while(commensIconBlockIterator.hasNext()) {
				Element commentsIconElement = commensIconBlockIterator.next();
				String commentsCountText = commentsIconElement.text();
				if (StringUtils.isInteger(commentsCountText)) {
					this.commentsCount = Long.parseLong(commentsCountText);
				}
				if (StringUtils.isValidCommentsCountText(commentsCountText)) {
					this.commentsCount = StringUtils.getCommentsCount(commentsCountText);
				}
			}
		}
	}
	
	public String getBlogHTML() {
		return this.blogHTML;
	}
	
	public List<Link> getOutlinks(){
		return this.outlinks;
	}
	
	public Long getCommentsCount() {
		return this.commentsCount;
	}
	
	public static void main(String[] args) {
		BlogParser blogParser = new BlogParser(
				"https://medium.freecodecamp.org/software-engineering-interviews-744380f4f2af");
		System.out.println(blogParser.getBlogHTML());
		System.out.println(blogParser.getCommentsCount());
	}

}
