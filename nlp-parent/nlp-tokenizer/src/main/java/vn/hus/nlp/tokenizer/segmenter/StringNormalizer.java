/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.tokenizer.segmenter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.nlp.segmenter
 * <p>
 * Nov 15, 2007, 11:51:08 PM
 * <p>
 * An accent normalizer for Vietnamese string. The purpose of
 * this class is to convert a syllable like "hòa" to "hoà",
 * since the lexicon contains only the later form.
 */
public final class StringNormalizer {

	private static final Map<String, Map<String, String>> map = new ConcurrentHashMap<>();
	private final String mapFile;

	private StringNormalizer(String mapFile) {
        map.computeIfAbsent(mapFile, this::init);
        this.mapFile = mapFile;
	}


	private Map<String, String> init(String mapFile) {
		InputStream stream = getClass().getResourceAsStream(mapFile);
		List<String> rules;
		try
		{
			rules = IOUtils.readLines(stream, "UTF-8");

			Map<String, String> map = new HashMap<>();
			for (int i = 0;i<rules.size();i++)
			{
				String rule = rules.get(i);

				String[] s = rule.split("\\s+");
				if (s.length == 2) {
					map.put(s[0], s[1]);
				} else {
					System.err.println("Wrong syntax in the map file " + mapFile + " at line " + i);
				}
			}
			return map;

		} catch (IOException e)
		{
			throw new IllegalArgumentException("Cannot load map file");
		}

	}


	/**
	 * @return an instance of the class.
	 */
	public static StringNormalizer getInstance() {
		return new StringNormalizer(IConstants.NORMALIZATION_RULES);
	}

	/**
	 * @param properties
	 * @return an instance of the class.
	 */
	public static StringNormalizer getInstance(Properties properties) {
		return new StringNormalizer(properties.getProperty("normalizationRules"));
	}

	/**
	 * Normalize a string.
	 * @return a normalized string
	 * @param s a string
	 */
	public String normalize(String s) {
		String result = s;
        Map<String, String> normalizationMap = map.get(mapFile);
        if (normalizationMap == null) {
            return result;
        }
        for (String from:  normalizationMap.keySet()) {
			String to = normalizationMap.get(from);
			if (result.contains(from)) {
				result = result.replace(from, to);
			}
		}
		return result;
	}

}
