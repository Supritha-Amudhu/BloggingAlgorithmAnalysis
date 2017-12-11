package edu.sfsu.bigdata.bloganalysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public static void generateEdgeFile(String nodeFilePath, String outputPath) {
		List<String> lruCache = new LinkedList<>();
		initializeCache(lruCache, nodeFilePath);
		try {
			FileReader freader = new FileReader(new File(nodeFilePath));
			FileWriter fwriter = new FileWriter(new File(outputPath));
			FileWriter blogStatsWriter = new FileWriter(new File("stats.csv"));
			BufferedReader bfreader = new BufferedReader(freader);
			Set<String> nodes = new HashSet<>();
			while(bfreader.ready()) {
				String blogUrl = bfreader.readLine();
				nodes.add(blogUrl);
				Blog blog = BlogParser.parseBlog(blogUrl);
				
				// populate out links
				for(Link link : blog.getOutlinks()) {
					nodes.add(link.getUrl());
					writeIfNotEqual(fwriter, blogUrl, link.getUrl());
				}
				// populate in links
				Set<String> inLinks = getRandomInLinks(lruCache);
				for(String inLink: inLinks) {
					writeIfNotEqual(fwriter, inLink, blogUrl);
				}
				blog.setInLinkCount(inLinks.size());
				String blogStats = blogUrl+","+blog.getBlogLength()+","
						+blog.getCommentsCount()+","+blog.getOutlinks().size()+","
						+inLinks.size()+","+BloggingAlgorithm.getBlogScore(blog)+"\n";
				System.out.println(blogStats);
				blogStatsWriter.write(blogStats);
				lruCache.add(blogUrl);
				lruCache.remove(0);
			}
			System.out.println("Nodes found = "+nodes.size());
			bfreader.close();
			blogStatsWriter.close();
			fwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Set<String> getRandomInLinks(List<String> lruCache) {
		int inLinksCount = rand.nextInt(lruCache.size()+1);
		Set<String> inlinks = new HashSet<>();
		inlinks.addAll(lruCache.subList(0,  inLinksCount));
		return inlinks;
	}

	private static void writeIfNotEqual(FileWriter fwriter, String url, String outlink) 
			throws IOException {
		if (fwriter != null) {
			if (!url.trim().equalsIgnoreCase(outlink.trim())) {
				fwriter.write(url+" "+outlink+"\n");
			}
		}
	}

	private static void initializeCache(List<String> lruCache, String nodeFilePath) {
		int cacheSize = 10;
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
