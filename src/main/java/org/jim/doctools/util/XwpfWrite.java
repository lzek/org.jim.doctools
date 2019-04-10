package org.jim.doctools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class XwpfWrite {
	/**
	 * 基本的写操作
	 * 
	 * @throws Exception
	 */
	Logger log = LogManager.getLogger(XwpfWrite.class);

	public void savaPickupQuestions(String Word, String file, Boolean blankWithUnderline) throws Exception {
		// 新建一个文档
		XWPFDocument doc = new XWPFDocument();
		// 创建一个段落
		XWPFParagraph para = doc.createParagraph();
		Word = Word.replace(" ", "  ");

		// 一个XWPFRun代表具有相同属性的一个区域。
		String[] a = Word.split("####");
		para.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun run = getNewNR(para);
		for (int i = 0; i < a.length; i++) {
			// run.setBold(true); //加粗
			String newword = a[i];
			if (blankWithUnderline) { // 空格加下划线
				Pattern r = Pattern.compile("( {1,}|\t)",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m = r.matcher(newword);
				while (m.find()) {
					if (m.start() > 0)
						run.setText(newword.substring(0, m.start()));
					run = getNewNR(para);
					run.setUnderline(UnderlinePatterns.SINGLE);
					run.setText(m.group());
					run = getNewNR(para);
					run.setUnderline(UnderlinePatterns.NONE);
					newword = newword.substring(m.end());
					m = r.matcher(newword);
				}
			}
			run.setText(newword);
			run.addCarriageReturn();
		}
		OutputStream os = new FileOutputStream(file);
		// 把doc输出到输出流
		doc.write(os);
		this.close(os);
		doc.close();
	}

	/**
	 * 拆分答案选项的私有函数
	 * 
	 * @param choiceword
	 * @param splitword
	 * @return
	 */
	private ArrayList<String> makeNewCA(String choiceword, String splitword) {
		ArrayList<String> tmpC = new ArrayList<String>();
		String[] tmp = choiceword.split("B" + splitword);
		if (tmp.length > 1) {
			tmpC.add(tmp[0].replace("A" + splitword, ""));
			tmp = tmp[1].split("C" + splitword);
			if (tmp.length > 1) {
				tmpC.add(tmp[0].replace("B" + splitword, ""));
				tmp = tmp[1].split("D" + splitword);
				if (tmp.length > 1) {
					tmpC.add(tmp[0].replace("C" + splitword, ""));
					tmp = tmp[1].split("E" + splitword);
					if (tmp.length > 1) {
						tmpC.add(tmp[0].replace("D" + splitword, ""));
						tmp = tmp[1].split("E" + splitword);
						if (tmp.length > 1) {
							tmpC.add(tmp[0].replace("E" + splitword, ""));
							tmp = tmp[1].split("F" + splitword);
							if (tmp.length > 1) {
								tmpC.add(tmp[0].replace("F" + splitword, ""));
							} else {
								tmpC.add(tmp[0].replace("F" + splitword, ""));
							}
						} else {
							tmpC.add(tmp[0].replace("E" + splitword, ""));
						}
					} else {
						tmpC.add(tmp[0].replace("D" + splitword, ""));
					}
				} else {
					tmpC.add(tmp[0].replace("C" + splitword, ""));
				}
			} else {
				tmpC.add(tmp[0].replace("B" + splitword, ""));
			}
		} else {
			tmpC.add(tmp[0].replace("A" + splitword, ""));
		}
		return tmpC;
	}

	/**
	 * 返回新的答案序列及答案
	 * 
	 * @param choiceword
	 * @param answer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getnewchoiceword(String choiceword, String answer) {
		String result_choiceword = "";
		answer = answer.replace("\n", "");
		String result_answer = "";
		Map<String, String> m_r = new HashMap<String, String>();
		ArrayList<String> tmpA = new ArrayList<String>();
		ArrayList<String> tmp = new ArrayList<String>();
		tmp = makeNewCA(choiceword, "．");
		Boolean donext = true;
		if (tmp != null && tmp.size() > 1) {
			tmpA = tmp;
			donext = false;
		}
		if (donext)
			tmp = makeNewCA(choiceword, ".");
		if (tmp != null && tmp.size() > 1) {
			tmpA = tmp;
			donext = false;
		}
		if (donext)
			tmp = makeNewCA(choiceword, "、");
		if (tmp != null && tmp.size() > 1) {
			tmpA = tmp;
			donext = false;
		}
		if (donext)
			tmp = makeNewCA(choiceword, " ");
		if (tmp != null && tmp.size() > 1) {
			tmpA = tmp;
			donext = false;
		}
		int answersize = tmpA.size();
		ArrayList<String> tmpB = new ArrayList<String>();
		tmpB = (ArrayList<String>) tmpA.clone();
		Random ra = new Random();
		String[] rc = new String[answersize];
		for (int i = 0; i < answersize; i++) {
			if (tmpA.size() > 1) {
				int index = ra.nextInt(tmpA.size());// 获取随机顺序
				result_choiceword = result_choiceword + NumToAlphabet.getAlphabet(i + 1) + "．" + tmpA.get(index);
				rc[i] = tmpA.get(index); // 记录新位置
				tmpA.remove(index);
			} else {
				result_choiceword = result_choiceword + NumToAlphabet.getAlphabet(i + 1) + "．" + tmpA.get(0);
				rc[i] = tmpA.get(0); // 记录新位置
			}
		}
		// 获取旧新位置对应表
		Map<Integer, Integer> oldtonew = new HashMap<Integer, Integer>();
		for (int i = 0; i < answersize; i++) {
			for (int j = 0; j < rc.length; j++) {
				if (tmpB.get(i).equals(rc[j])) {
					oldtonew.put(i + 1, j + 1); // NumToAlphabet 字母从1开始计算，所以索引需要加一
				}
			}
		}
		// 按照答案查询对应表，再生成对应的新位置
		for (int i = 1; i < 7; i++) {
			if (answer.indexOf(NumToAlphabet.getAlphabet(i)) > -1) {
				if (result_answer.equals("")) {
					result_answer = NumToAlphabet.getAlphabet(oldtonew.get(i));
				} else {
					result_answer = result_answer + "、" + NumToAlphabet.getAlphabet(oldtonew.get(i));
				}
			}
		}

		String tmp_result_answer = "";
		for (int i = 1; i < 7; i++) {
			if (result_answer.indexOf(NumToAlphabet.getAlphabet(i)) > -1) {
				if (tmp_result_answer.equals("")) {
					tmp_result_answer = NumToAlphabet.getAlphabet(i);
				} else {
					tmp_result_answer = tmp_result_answer + "、" + NumToAlphabet.getAlphabet(i);
				}
			}
		}
		result_answer = tmp_result_answer;
		m_r.put(result_choiceword, result_answer);
		return m_r;
	}

	/**
	 * 拆包问题、选项和答案，调用生成函数获得新的选项组合以及答案
	 * 
	 * @param filePath
	 * @param qa
	 * @param type
	 * @param count
	 * @throws Exception
	 */
	public void setQuestions(String filePath, Map<String, Map<String, String>> qa, String type, int count)
			throws Exception {
		Boolean bold = false;
		int fontsize = 12;
		String qaword = "";
		String choiceword = "";
		String answer = "";
		for (String key : qa.keySet()) {
			qaword = key;

			Map<String, String> ca = qa.get(key);
			for (String k : ca.keySet()) {
				choiceword = k;
				answer = ca.get(k);
			}
			if (!choiceword.equals("")) {
				Map<String, String> newca = getnewchoiceword(choiceword, answer);
				for (String k : newca.keySet()) {
					choiceword = k;
					answer = newca.get(k);
				}
			}
			String allword = "";
			String allword_answer = "";
			switch (type) {
			case "c_singleChoice":
			case "c_multipleChoice":
			case "c_indefiniteChoice":
				allword = count + "、" + qaword + "####" + choiceword;
				// String answerswitch=AppProperties.get("file_answer");
				// if (answerswitch.contains("true")) allword=allword+"####答案："+answer;
				Pattern r = Pattern.compile("([（\\(][ \\r\\n]+[\\)）])", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m = r.matcher(qaword);
				String qawordAnswer = qaword;
				while (m.find()) {
					qawordAnswer = qawordAnswer.replace(m.group(), "( " + answer + " )");
				}
				allword_answer = count + "、" + qawordAnswer + "####" + choiceword;
				break;
			case "c_completion":
				allword = count + "、" + qaword;
				allword_answer = count + "、" + qaword + "####答案：" + answer;
				break;
			case "c_TFChoice":
				allword = count + "、" + qaword + "(   )";
				allword_answer = count + "、" + qaword + "  (  " + answer + " )";
				break;
			case "c_essayQuestion":
				allword = count + "、" + qaword + "#### " + " #### " + " #### " + " #### " + " #### " + " #### "
						+ " #### " + " #### ";
				allword_answer = count + "、" + qaword + "####答案：" + answer;
				break;
			}
			formatThenSave(filePath, allword, bold, fontsize, type);
			formatThenSave(filePath + "_答案", allword_answer, bold, fontsize, type);
		}
	}

	public void setQuestionsTitle(String filePath, String word) throws Exception {
		Boolean bold = true;
		int fontsize = 18;
		formatThenSave(filePath, word, bold, fontsize, "title");
	}

	public void formatThenSave(String filePath, String word, Boolean bold, int fontsize, String type) throws Exception {
		File targetFile = new File(filePath + ".docx");
		File ppath = targetFile.getParentFile();
		if (!ppath.exists()) {
			ppath.mkdirs();
		}
		InputStream is = new FileInputStream(targetFile);
		XWPFDocument doc = new XWPFDocument(is);
		XWPFParagraph np = doc.createParagraph();
		/*
		 * CTP p = np.getCTP(); CTPPr pPr = p.addNewPPr(); //设置行间距 CTSpacing pSpacing =
		 * pPr.getSpacing() != null ? pPr.getSpacing(): pPr.addNewSpacing();
		 * pSpacing.setLineRule(STLineSpacingRule.AT_LEAST);//行间距类型：最小值
		 * pSpacing.setLine(new BigInteger("360"));//此处1倍行间距为240
		 * pSpacing.setBeforeLines(new BigInteger("20"));//段前0.2
		 * pSpacing.setAfterLines(new BigInteger("10"));//段后0.1
		 */
		/*
		 * //设置字体 CTRPr pRpr = run.getRPr(); CTFonts fonts = pRpr.isSetRFonts() ?
		 * pRpr.getRFonts() : pRpr.addNewRFonts(); fonts.setAscii("Times New Roman");
		 * fonts.setEastAsia("黑体"); fonts.setHAnsi("黑体"); // 设置字体大小 CTHpsMeasure sz =
		 * pRpr.isSetSz() ? pRpr.getSz() : pRpr.addNewSz(); sz.setVal(new
		 * BigInteger("24"));
		 * 
		 * CTHpsMeasure szCs = pRpr.isSetSzCs() ? pRpr.getSzCs() : pRpr.addNewSzCs();
		 * szCs.setVal(new BigInteger("24"));
		 */
		String[] nws = word.split("####");
		for (String nw : nws) {
			XWPFRun nr = getNewNR(np);
			nr.setBold(bold);
			nr.setFontSize(fontsize);

			if (type.equals("c_completion")) { // 填空题，空格加下划线
				Pattern r = Pattern.compile("( {1,}|\t)",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m = r.matcher(nw);
				while (m.find()) {
					if (m.start() > 0)
						nr.setText(nw.substring(0, m.start() - 1));
					nr = getNewNR(np);
					nr.setUnderline(UnderlinePatterns.SINGLE);
					nr.setText(m.group());
					nr = getNewNR(np);
					nr.setUnderline(UnderlinePatterns.NONE);
					nw = nw.substring(m.end());
					m = r.matcher(nw);
				}
			}
			nr.setText(nw);
			nr.addCarriageReturn();
		}
		OutputStream os = new FileOutputStream(targetFile);
		doc.write(os);
		doc.close();
		this.close(os);
		this.close(is);
	}

	// 缺省段落格式
	public XWPFRun getNewNR(XWPFParagraph np) {
		XWPFRun nr = np.createRun();
		String XWPFRun_TextPosition = AppProperties.get("XWPFRun_TextPosition");
		Integer nr_TextPosition = 0;
		if (XWPFRun_TextPosition != null) {
			nr_TextPosition = Integer.parseInt(XWPFRun_TextPosition.trim());
		}
		String XWPFRun_FontFamily = AppProperties.get("XWPFRun_FontFamily");
		if (XWPFRun_FontFamily == null || XWPFRun_FontFamily.equals(""))
			XWPFRun_FontFamily = "宋体";

		nr.setTextPosition(nr_TextPosition); // 设置行距
		nr.setFontFamily(XWPFRun_FontFamily); // 设置字体

		return nr;
	}

	public void setExamTitle(String filePath, Map<String, String> params) throws Exception {
		String output = filePath + ".docx";
		File targetFile = new File(output);
		File ppath = targetFile.getParentFile();
		if (!ppath.exists()) {
			ppath.mkdirs();
		}
		String templatepath = AppProperties.get("template");
		if (templatepath == null || templatepath.equals("")) {
			templatepath = "template.docx";
		}
		replaceInDoc(templatepath, output, params);
	}

	public void replaceInDoc(String inputDocx, String outputdocx, Map<String, String> params) throws Exception {
		InputStream is = new FileInputStream(inputDocx);
		XWPFDocument doc = new XWPFDocument(is);
		this.close(is);
		// 替换段落里面的变量
		this.replaceInAllParagraphs(doc.getParagraphs(), params);
		// 替换表格里面的变量
		this.replaceInTables(doc.getTables(), params);
		OutputStream os = new FileOutputStream(outputdocx);
		doc.write(os);
		doc.close();
		this.close(os);
	}

	/**
	 * 替换所有段落中的标记
	 *
	 * @param xwpfParagraphList
	 * @param params
	 */
	public void replaceInAllParagraphs(List<XWPFParagraph> xwpfParagraphList, Map<String, String> params) {
		for (XWPFParagraph paragraph : xwpfParagraphList) {
			if (paragraph.getText() == null || paragraph.getText().equals(""))
				continue;
			for (String key : params.keySet()) {
				if (paragraph.getText().contains(key)) {
					replaceInParagraph(paragraph, key, params.get(key));
				}
			}
		}
	}

	/**
	 * 替换段落中的字符串
	 *
	 * @param xwpfParagraph
	 * @param oldString
	 * @param newString
	 */
	public void replaceInParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
		Map<String, Integer> pos_map = findSubRunPosInParagraph(xwpfParagraph, oldString);
		if (pos_map != null) {
			log.debug("start_pos:" + pos_map.get("start_pos"));
			log.debug("end_pos:" + pos_map.get("end_pos"));

			List<XWPFRun> runs = xwpfParagraph.getRuns();
			XWPFRun modelRun = runs.get(pos_map.get("end_pos"));
			XWPFRun xwpfRun = xwpfParagraph.insertNewRun(pos_map.get("end_pos") + 1);
			xwpfRun.setText(newString);
			log.debug("字体大小：" + modelRun.getFontSize());
			if (modelRun.getFontSize() != -1)
				xwpfRun.setFontSize(modelRun.getFontSize());// 默认值是五号字体，但五号字体getFontSize()时，返回-1
			xwpfRun.setFontFamily(modelRun.getFontFamily());
			for (int i = pos_map.get("end_pos"); i >= pos_map.get("start_pos"); i--) {
				log.debug("remove run pos in :" + i);
				xwpfParagraph.removeRun(i);
			}
		}
	}

	/**
	 * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
	 *
	 * @param xwpfParagraph
	 * @param substring
	 * @return
	 */
	public Map<String, Integer> findSubRunPosInParagraph(XWPFParagraph xwpfParagraph, String substring) {

		List<XWPFRun> runs = xwpfParagraph.getRuns();
		int start_pos = 0;
		int end_pos = 0;
		String subtemp = "";
		for (int i = 0; i < runs.size(); i++) {
			subtemp = "";
			start_pos = i;
			for (int j = i; j < runs.size(); j++) {
				if (runs.get(j).getText(runs.get(j).getTextPosition()) == null)
					continue;
				subtemp += runs.get(j).getText(runs.get(j).getTextPosition());
				if (subtemp.equals(substring)) {
					end_pos = j;
					Map<String, Integer> map = new HashMap<>();
					map.put("start_pos", start_pos);
					map.put("end_pos", end_pos);
					return map;
				}
			}
		}
		return null;
	}

	/**
	 * 替换所有的表格
	 *
	 * @param xwpfTableList
	 * @param params
	 */
	public void replaceInTables(List<XWPFTable> xwpfTableList, Map<String, String> params) {
		for (XWPFTable table : xwpfTableList) {
			replaceInTable(table, params);

		}
	}

	/**
	 * 替换一个表格中的所有行
	 *
	 * @param xwpfTable
	 * @param params
	 */
	public void replaceInTable(XWPFTable xwpfTable, Map<String, String> params) {
		List<XWPFTableRow> rows = xwpfTable.getRows();
		replaceInRows(rows, params);
	}

	/**
	 * 替换表格中的一行
	 *
	 * @param rows
	 * @param params
	 */
	public void replaceInRows(List<XWPFTableRow> rows, Map<String, String> params) {
		for (int i = 0; i < rows.size(); i++) {
			XWPFTableRow row = rows.get(i);
			replaceInCells(row.getTableCells(), params);
		}
	}

	/**
	 * 替换一行中所有的单元格
	 *
	 * @param xwpfTableCellList
	 * @param params
	 */
	public void replaceInCells(List<XWPFTableCell> xwpfTableCellList, Map<String, String> params) {
		for (XWPFTableCell cell : xwpfTableCellList) {
			replaceInCell(cell, params);
		}
	}

	/**
	 * 替换表格中每一行中的每一个单元格中的所有段落
	 *
	 * @param cell
	 * @param params
	 */
	public void replaceInCell(XWPFTableCell cell, Map<String, String> params) {
		List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
		replaceInAllParagraphs(cellParagraphs, params);
	}

	/**
	 * 关闭输入流
	 * 
	 * @param is
	 */
	private void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭输出流
	 * 
	 * @param os
	 */
	private void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
