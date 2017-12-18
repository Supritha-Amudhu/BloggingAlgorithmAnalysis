package edu.sfsu.bigdata.bloganalysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.sfsu.bigdata.bloganalysis.core.BloggingAlgorithm;
import edu.sfsu.bigdata.bloganalysis.model.Blog;
import edu.sfsu.bigdata.bloganalysis.model.Link;
import edu.sfsu.bigdata.bloganalysis.parser.BlogParser;

public class EdgeFileGenerator {
	private static final Random rand = new Random();
	private static int edgeCount = 0;
	
	/**
	 * The file blogs.txt gets parsed, and an Edge file edges.txt gets generated which has Outlinks for 
	 * every node mapped in it. PageRank algorithm takes the values from edges.txt to run its algorithm.
	 * @param nodeFilePath
	 * @param outputPath
	 */
	public static void generateEdgeFile(String nodeFilePath, String outputPath) {
		List<String> lruCache = new LinkedList<>();
		initializeCache(lruCache, nodeFilePath);
		try {
			FileReader freader = new FileReader(new File(nodeFilePath));
			FileWriter fwriter = new FileWriter(new File(outputPath));
			FileWriter blogStatsWriter = new FileWriter(new File("stats.csv"));
			BufferedReader bfreader = new BufferedReader(freader);
			Set<String> nodes = new HashSet<>();
			List<String> edgeList = new ArrayList<String>();
			blogStatsWriter.write("URL, Length, Number of Comments, Outlinks, Inlinks, Blog Score\n");
			while(bfreader.ready()) {
				String blogUrl = bfreader.readLine();
				nodes.add(blogUrl);
				Blog blog = BlogParser.parseBlog(blogUrl);
				
				// populate Outlinks
				for(Link link : blog.getOutlinks()) {
					nodes.add(link.getUrl());
					String edge = getStringIfNotEqual(blogUrl, link.getUrl());
					if (edge != null)
						edgeList.add(edge);
				}
				// populate inlinks
				Set<String> inLinks = getRandomInLinks(lruCache);
				for(String inLink: inLinks) {
					String edge = getStringIfNotEqual(inLink, blogUrl);
					if (edge != null)
						edgeList.add(edge);
				}
				blog.setInLinkCount(inLinks.size());
				long blogLength = blog.getBlogLength();
				if(!(blogLength > 70)){
					continue;
				}
				String blogStats = blogUrl+","+blogLength+","
						+blog.getCommentsCount()+","+blog.getOutlinks().size()+","
						+inLinks.size()+","+BloggingAlgorithm.getBlogScore(blog)+"\n";
				System.out.println(blogStats);
				blogStatsWriter.write(blogStats);
				lruCache.add(blogUrl);
				lruCache.remove(0);
			}
			System.out.println("Nodes found = "+nodes.size());
			fwriter.write(nodes.size()+"\n");
			for(String edge: edgeList){
				fwriter.write(edge);
			}
			fwriter.close();
			bfreader.close();
			blogStatsWriter.close();
			fwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Randomized Inlinks assignment to the blogs, with 4 of the blogs having a high value of Inlinks.
	 * @param lruCache
	 * @return
	 */
	private static Set<String> getRandomInLinks(List<String> lruCache) {
		int inLinksCount = rand.nextInt(10);
		Set<String> inlinks = new HashSet<>();
		//Assigning a large value of inlinks for the first 3 links
		if(edgeCount < 5){
			inLinksCount += 90;
		}
		edgeCount++;
		inlinks.addAll(lruCache.subList(0,  inLinksCount));
		return inlinks;
	}

	/**
	 * Returns the URL of a blog mapped to its Outlink
	 * @param url
	 * @param outlink
	 * @return
	 * @throws IOException
	 */
	private static String getStringIfNotEqual(String url, String outlink) 
			throws IOException {
		if (!url.trim().equalsIgnoreCase(outlink.trim())) {
			return url+" "+outlink+"\n";
		}
		return null;
	}

	/**
	 * An LRU Cache stores the recently parsed blogs
	 * @param lruCache
	 * @param nodeFilePath
	 */
	private static void initializeCache(List<String> lruCache, String nodeFilePath) {
		int cacheSize = 200;
		try {
			FileReader freader = new FileReader(new File(nodeFilePath));
			BufferedReader bfreader = new BufferedReader(freader);
			while(bfreader.ready() && cacheSize > 0) {
				lruCache.add(bfreader.readLine());
				cacheSize --;
			}
			bfreader.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		EdgeFileGenerator.generateEdgeFile("blogs.txt", "edges.txt");
	}
}
