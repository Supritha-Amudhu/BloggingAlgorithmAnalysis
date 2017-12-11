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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.sfsu.bigdata.bloganalysis.model.Blog;
import edu.sfsu.bigdata.bloganalysis.model.Link;
import edu.sfsu.bigdata.bloganalysis.util.StringUtils;

public class BlogParser {

	private static final String GET = "GET";
	
	public static Blog parseBlog(String blogURL) {
		try {
			Blog blog = new Blog(blogURL);
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
		      blog.setBlogHTML(blogMarkup.toString());
		      blog.setHtmlDocument(Jsoup.parse(blogMarkup.toString()));
		      List<Link> outlinks = parseLinks(blog);
		      blog.setOutlinks(outlinks);
		      blog.setCommentsCount(parseComments(blog));
		      blog.setBlogLength(getBlogLength(blog));
		      return blog;
		} catch (MalformedURLException e) {
			System.out.println("Error creating URL.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error opening connection. ");
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean isValidHTMLDocument(Blog blog) {
		return blog.getHtmlDocument() != null && blog.getHtmlDocument().body() != null;
	}
	
	private static List<Link> parseLinks(Blog blog) {
		if (isValidHTMLDocument(blog)) {
			List<Link> outlinks = new ArrayList<>();
			Element body = blog.getHtmlDocument().body();
			Elements links = body.select("section.section--body a[href]");
			Iterator<Element> linksIterator = links.iterator();
			while(linksIterator.hasNext()) {
				Element link = linksIterator.next();
				Link outlink = new Link();
				outlink.setLinkText(link.text());
				String outlinkURL = link.attr("href");
				if(StringUtils.isNotEmpty(outlinkURL)) {
					outlink.setUrl(outlinkURL);
				}
				outlinks.add(outlink);
			}
			return outlinks;
		}
		return null;
	}
	
	private static long parseComments(Blog blog) {
		long commentsCount = 0;
		if (isValidHTMLDocument(blog)) {
			Element body = blog.getHtmlDocument().body();
			Element actionFooter = body.selectFirst(".js-postActionsFooter");
			if (actionFooter != null) {
				Elements commentsIconBlock = actionFooter
						.select("button[data-action=scroll-to-responses]");
				if (commentsIconBlock != null) {
					Iterator<Element> commensIconBlockIterator = commentsIconBlock.iterator();
					while(commensIconBlockIterator.hasNext()) {
						Element commentsIconElement = commensIconBlockIterator.next();
						String commentsCountText = commentsIconElement.text();
						if (StringUtils.isInteger(commentsCountText)) {
							commentsCount = Long.parseLong(commentsCountText);
						}else if (StringUtils.isValidCommentsCountText(commentsCountText)) {
							commentsCount = StringUtils.getCommentsCount(commentsCountText);
						}
					}
				}
			}
		}
		return commentsCount;
	}
	
	private static long getBlogLength(Blog blog) {
		long blogLength = 0;
		if (isValidHTMLDocument(blog)) {
			Element body = blog.getHtmlDocument().body();
			if (body != null) {
				Elements paragraphs = body.select("section.section--body p");
				if (paragraphs != null) {
					Iterator<Element> paraIterator = paragraphs.iterator();
					while(paraIterator.hasNext()) {
						Element paragraph = paraIterator.next();
						blogLength += paragraph.text().split(" ").length;
					}
				}
			}
		}
		return blogLength;
	}
	
	public static void main(String[] args) {
		Blog blog = BlogParser.parseBlog(
				"https://medium.freecodecamp.org/software-engineering-interviews-744380f4f2af");
		System.out.println(blog.getBlogHTML());
		for (Link link: blog.getOutlinks()) {
			System.out.println(link);
		}
		System.out.println(blog.getCommentsCount());
	}

}
