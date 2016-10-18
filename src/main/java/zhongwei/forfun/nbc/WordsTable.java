/**
 * 
 */
package zhongwei.forfun.nbc;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
* <p>Title: WordsTable.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: Zhongwei</p>
* @author Zhongwei
* @date 2016年10月17日
* @version 1.0
*/
public class WordsTable {
	public static final double THRESHOLD_STOP = 0.3;
	
	private List<String> stopWords;
	
	private List<String> labels;
	private List<Article> articles;
	private Map<String, List<Article>> articlesLabeled;
	
	private List<String> wordsAll;
	private Map<String, Integer> wordsCountAll;
	
	private Map<String, List<String>> wordsLabeled;//label->words
	private Map<String, Map<String, Integer>> wordsCountLabeled;//label->(word, count)
	
	private Map<String, Integer> articlesCountAll;//word->articles
	private Map<String, Map<String, Integer>> articlesCountLabeled;//word->(label, articles)
	
	public WordsTable(
			List<String> labels,
			List<Article> articles,
			Map<String, List<Article>> articlesLabeled
			) {
		this.labels = labels;
		this.articles = articles;
		this.articlesLabeled = articlesLabeled;
		
		this.wordsAll = new LinkedList<String>();
		this.wordsLabeled = new HashMap<String, List<String>>();
		
		this.wordsCountAll = new HashMap<String, Integer>();
		this.wordsCountLabeled = new HashMap<String, Map<String, Integer>>();
		
		this.articlesCountAll = new HashMap<String, Integer>();
		this.articlesCountLabeled = new HashMap<String, Map<String, Integer>>();
		
		for(String label : labels) {
			List<String> wordsOfLabel = new LinkedList<String>();
			Map<String, Integer> wordsCountOfLabel = new TreeMap<String, Integer>();
			for(Article article: this.articlesLabeled.get(label)) {
				for(String word : article.getWords()) {
					this.wordsAll.add(word);
					wordsOfLabel.add(word);
					
					int countAll = this.wordsCountAll.containsKey(word) ? this.wordsCountAll.get(word) : 0;
					this.wordsCountAll.put(word, ++countAll);
					
					int countLabel = wordsCountOfLabel.containsKey(word) ? wordsCountOfLabel.get(word) : 0;
					wordsCountOfLabel.put(word, ++countLabel);
				}
			}
			this.wordsCountLabeled.put(label, wordsCountOfLabel);
			
			wordsOfLabel = WordsTable.sortMap(wordsCountOfLabel, new Comparator<Object>() {  
	            public int compare(Object o1, Object o2) {  
	            	Entry<String, Integer> obj1 = (Entry<String, Integer>) o1;  
	            	Entry<String, Integer> obj2 = (Entry<String, Integer>) o2;  
	                return (obj2.getValue()).compareTo(obj1.getValue());  
	                }  
	        });
			
			this.wordsLabeled.put(label, wordsOfLabel);
		}
		this.wordsAll = WordsTable.sortMap(wordsCountAll, new Comparator<Object>() {  
            public int compare(Object o1, Object o2) {  
            	Entry<String, Integer> obj1 = (Entry<String, Integer>) o1;  
            	Entry<String, Integer> obj2 = (Entry<String, Integer>) o2;  
                return (obj2.getValue()).compareTo(obj1.getValue());  
                }  
        });
		
		for(String word : this.wordsAll) {
			int countAll = 0;
			Map<String, Integer> articlesCountOfLabel = new TreeMap<String, Integer>();
			for(String label : this.labels) {
				int countLabel = 0;
				for(Article article : this.articlesLabeled.get(label)) {
					if(article.getWords().contains(word)) {
						countAll++;
						countLabel++;						
						
					}
				}
				articlesCountOfLabel.put(label, countLabel);
			}
			this.articlesCountLabeled.put(word, articlesCountOfLabel);
			this.articlesCountAll.put(word, countAll);
		}
		
		this.stopWords = generateStopWords();
		filterStopWords(this.stopWords);
	}

	/**
	 * @param label
	 * @return
	 */
	public List<String> getWordsLabeled(String label) {
		return this.wordsLabeled.get(label);
	}
	
	/**
	 * @param word
	 * @return
	 */
	public int getArticlesNumOfWord(String word) {
		return this.articlesCountAll.get(word);
	}
	

	public Map<String, Integer> getWordsCountMap() {
		return this.wordsCountAll;
	}
	
	public List<String> getWords() {
		return this.wordsAll;
	}
	
	public double getSize() {
		return this.wordsAll.size();
	}
	
	public boolean containsWord(String word) {
		return this.wordsAll.contains(word);
	}
	
	public List<String> getStopWords() {
		return this.stopWords;
	}
	
	private List<String> generateStopWords() {
		List<String> result = new LinkedList<String>();
		
		int numOfLabels = this.labels.size();
		
		for(String word : this.wordsAll) {
			int count = 0;
			for(String label : this.labels) {
				if(wordsCountLabeled.get(label).containsKey(word))
					count++;
			}
			if((double)count/numOfLabels >= THRESHOLD_STOP) {
				result.add(word);
			}
		}
		
		return result;
	}
	
	private void filterStopWords(List<String> stopWords) {
		for(Article article : this.articles) {
			article.filter(stopWords);
		}
		
		this.wordsAll.removeAll(stopWords);
		for(String label : this.labels) {
			List<String> words = this.wordsLabeled.get(label);
			words.removeAll(stopWords);
			this.wordsLabeled.put(label, words);
			
			Map<String, Integer> map = this.wordsCountLabeled.get(label);
			for(String word : stopWords) {
				map.remove(word);
			}
		}
		
		for(String word : stopWords) {
			this.wordsCountAll.remove(word);
			this.articlesCountAll.remove(word);
			this.articlesCountLabeled.remove(word);
		}
	}
	
	public static <T> List<String> sortMap(Map<String, T> map, Comparator comparator) {
		List<Entry<String, T>> list = new LinkedList<Entry<String, T>>(map.entrySet());
		
		Collections.sort(list, comparator);
		
		List<String> listSorted = new LinkedList<String>();
		for(Entry<String, T> entry : list) {
			listSorted.add(entry.getKey());
		}
		return listSorted;
	}
}
