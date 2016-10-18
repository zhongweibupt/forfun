/**
 * 
 */
package zhongwei.forfun.nbc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
* <p>Title: Trainer.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月17日
* @version 1.0
*/
public class Trainer {
	public static final double LAPLACE_SMOOTH_PARA = 1.0;
	
	private List<String> labels;
	private List<Article> articles;
	private Map<String, List<Article>> articlesLabeled;
	
	private WordsTable wordsTable;
	
	public Trainer() {
		this.articles = new LinkedList<Article>();
		this.labels = new LinkedList<String>();
		this.articlesLabeled = new TreeMap<String, List<Article>>();
		this.wordsTable = null;
	}
	
	public void init() {
		this.articles.clear();
		this.labels.clear();
		this.articlesLabeled.clear();
		this.wordsTable = null;
	}
	
	public void train(List<Article> articles, List<String> labels) {
		this.articles = articles;
		this.labels = labels;
		this.articlesLabeled = labelArticles(this.articles);
		this.wordsTable = new WordsTable(this.labels, this.articles, this.articlesLabeled);
	}
	
	public String clustering(Article article) {
		List<String> results = WordsTable.sortMap(computeConPro(article, this.labels));
		return results.get(0);
	}
	
	public Map<String, Double> computeConPro(Article article, List<String> labels) {
		Map<String, Double> result = new TreeMap<String, Double>();
		for(String label : labels) {
			result.put(label, computeConProOfArticle(article, label));
		}
		return result;
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
	
	public double computeConProOfArticle(Article article, String label) {
		double result = 1.0;
		
		for(String word : article.getWords()) {
			result *= computeConProOfWord(word, label);
		}
		
		return result;
	}
	
	public double computeConProOfWord(String word, String label) {
		double result = 0.0;
		
		double tfIdfSum = 0.0;
		List<Article> articles = this.articlesLabeled.get(label);
		for(Article article : articles) {
			tfIdfSum += computeTfIdf(word, article);
		}
		
		double tfIdfAll = 0.0;
		List<String> words = this.wordsTable.getWordsLabeled(label);
		for(String s : words) {
			for(Article article : articles) {
				tfIdfAll += computeTfIdf(s, article);
			}
		}
		
		result = (tfIdfSum + LAPLACE_SMOOTH_PARA) / (tfIdfAll + LAPLACE_SMOOTH_PARA * wordsTable.getSize());
		
		return result;
	}
	
	public double computeTfIdf(String word, Article article) {
		double result = 0.0;
		
		if(!this.wordsTable.getWordsCountMap().containsKey(word)) {
			return result;
		}
		
		double tfNormalize = (double)article.getFrequencyOfWord(word)/article.getLength();
		int n = this.articles.size();
		int nContainsWord = this.wordsTable.getArticlesNumOfWord(word);
		
		result = tfNormalize * Math.log((double)n/nContainsWord);
		
		return result;
	}

}
