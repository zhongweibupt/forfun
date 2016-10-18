/**
 * 
 */
package zhongwei.forfun.nbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
* <p>Title: Classifier.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月17日
* @version 1.0
*/
public class Classifier {
	public static final double LAPLACE_SMOOTH_PARA = 1.0;
	
	private List<String> labels;
	private List<Article> articles;
	private Map<String, List<Article>> articlesLabeled;
	
	private Map<String, Double> tfIdfAllMap;
	
	private List<String> stopWords;
	
	private WordsTable wordsTable;
	
	public Classifier() {
		this.articles = new LinkedList<Article>();
		this.labels = new LinkedList<String>();
		this.articlesLabeled = new TreeMap<String, List<Article>>();
		this.wordsTable = null;
		
		this.tfIdfAllMap = new TreeMap<String, Double>();
	}
	
	public void init() {
		this.articles.clear();
		this.labels.clear();
		this.articlesLabeled.clear();
		this.wordsTable = null;
		
		this.tfIdfAllMap.clear();
	}
	
	private List<Article> readArticles(String articlesDataPath) throws IOException {
		File file = new File(articlesDataPath);
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("[\n]");
		
		List<Article> articles = new LinkedList<Article>();
		
		int i = 0;
		while (scanner.hasNext()) {
			if(i == 0) {
				scanner.next();
			}
			String line = scanner.next();
			
			int index = line.indexOf(',');
			String label = line.substring(0, index);
			String text = line.substring(index + 1, line.length());
			
			Article article = new Article(text);
			article.setLabel(label);
			article.wordsCount();
			
			articles.add(article);
			i++;
	    }
		return articles;
	}
	
	private void readLabels(String labelsDataPath) throws FileNotFoundException {
		File file = new File(labelsDataPath);
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter("[\n]");
		
		int i = 0;
		while (scanner.hasNext()) {
			//if(i == 0) {
				//for(int j = 0; j <= dimension; j++)
					//scanner.next();
			//}
			String label = scanner.next();
			this.labels.add(label);
			i++;
	    }
	}
	
	public void train(String articlesDataPath) throws IOException {
		this.articles = readArticles(articlesDataPath);
		for(Article article : this.articles) {
			this.labels.add(article.getLabel());
		}
		
		this.labels = new LinkedList<String>(new HashSet<String>(this.labels));

		this.articlesLabeled = labelArticles(this.articles);
		this.wordsTable = new WordsTable(this.labels, this.articles, this.articlesLabeled);
		this.stopWords = this.wordsTable.getStopWords();
		
		this.computeTfIdfAll();
	}
	
	private Map<String, List<Article>> labelArticles(List<Article> articles) {
		Map<String, List<Article>> articlesLabeled = new TreeMap<String, List<Article>>();
		for(Article article : articles) {
			List<Article> tmp = new LinkedList<Article>();
			if(articlesLabeled.containsKey(article.getLabel())) {
				tmp = articlesLabeled.get(article.getLabel());
			}
			
			tmp.add(article);
			articlesLabeled.put(article.getLabel(), tmp);
		}
		
		return articlesLabeled;
	}
	
	public void classify(String testDataPath) throws IOException {
		List<Article> test = readArticles(testDataPath);
		
		//TODO
		int curr = 0;
		for(Article article : test) {
			String label = classifyArticle(article);
			System.out.println("=========RESULT=========");
			System.out.println(label);
			System.out.println("========================");
			
			if(label.equals("Concur issues") || label == "Concur issues") {
				curr++;
			}
		}
		System.out.println("CURRENT:" + (double)curr/test.size());
	}
	
	public String classifyArticle(Article article) {
		List<String> results = WordsTable.sortMap(computeConPro(article, this.labels), new Comparator<Object>() {  
            public int compare(Object o1, Object o2) {  
            	Entry<String, Double> obj1 = (Entry<String, Double>) o1;  
            	Entry<String, Double> obj2 = (Entry<String, Double>) o2;  
                return (obj2.getValue()).compareTo(obj1.getValue());  
                }  
        });
		return results.get(0);
	}
	
	public Map<String, Double> computeConPro(Article article, List<String> labels) {
		Map<String, Double> result = new TreeMap<String, Double>();
		for(String label : labels) {
			result.put(label, computeConProOfArticle(article, label));
		}
		return result;
	}
	
	public double computeConProOfArticle(Article article, String label) {
		double result = 1.0;
		
		for(String word : article.getWords()) {
			result *= computeConProOfWord(word, label);
		}
		
		//TODO
		System.out.println(label + "," + result);
		
		return result;
	}
	
	public double computeConProOfWord(String word, String label) {
		double result = 0.0;
		
		double tfIdfSum = 0.0;
		List<Article> articles = this.articlesLabeled.get(label);
		for(Article article : articles) {
			tfIdfSum += computeTfIdf(word, article);
		}
		
		result = wordsTable.getSize() * (tfIdfSum + LAPLACE_SMOOTH_PARA) / (this.tfIdfAllMap.get(label) + LAPLACE_SMOOTH_PARA * wordsTable.getSize());
		
		return result;
	}
	
	private void computeTfIdfAll() {
		for(String label : this.labels) {
			double tfIdfAll = 0.0;
			
			List<Article> articles = this.articlesLabeled.get(label);
			List<String> words = this.wordsTable.getWordsLabeled(label);
			
			for(String s : words) {
				for(Article article : articles) {
					tfIdfAll += computeTfIdf(s, article);
				}
			}
			
			this.tfIdfAllMap.put(label, tfIdfAll);
		}
	}
	
	public double computeTfIdf(String word, Article article) {
		double result = 0.0;
		
		if(!this.wordsTable.containsWord(word)) {
			return result;
		}
		
		double tfNormalize = (double)article.getFrequencyOfWord(word)/(article.getLength() + 1);
		int n = this.articles.size();
		int nContainsWord = this.wordsTable.getArticlesNumOfWord(word);
		
		result = tfNormalize * Math.log((double)n/nContainsWord);
		return result;
	}
	
	public List<String> getStopWords() {
		return this.stopWords;
	}
	
	public WordsTable getWordTable() {
		return this.wordsTable;
	}
	
	public static void main(String args[]) throws IOException {
		Classifier classifier = new Classifier();
		classifier.init();
		classifier.train("C:\\Users\\zhwei\\workspace\\forfun\\data\\articles.csv");
		classifier.classify("C:\\Users\\zhwei\\workspace\\forfun\\data\\Concur issues.csv");
		
	}

}
