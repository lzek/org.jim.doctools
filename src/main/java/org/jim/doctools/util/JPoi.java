package org.jim.doctools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class JPoi {
	static Logger log = LogManager.getLogger(JPoi.class);

	public String getContext(String filepath) throws IOException {
		File f = new File(filepath);
		String word = null;
		if (filepath.endsWith(".docx")) {
			word = contextOfDocx(f);
		} else {
			word = contextOfDoc(f);
		}
		return word;
	}

	public ArrayList<String> getContextRows(String filepath) throws Exception {
		ArrayList<String> docRowsList = new ArrayList<String>();
		File f = new File(filepath);
		String word = null;
		if (filepath.endsWith(".docx")) {
			docRowsList = getRowsOfDocx(f);
		} else {
			log.error("请把输入文档设置成docx格式");
			throw new Exception("请把输入文档设置成docx格式");
		}
		return docRowsList;
	}

	private String contextOfDoc(File file) throws IOException {
		String str = "";
		FileInputStream fis = new FileInputStream(file);
		HWPFDocument doc = new HWPFDocument(fis);
		str = doc.getDocumentText();
		doc.close();
		fis.close();

		return str;
	}

	private String contextOfDocx(File file) throws IOException {
		String str = "";

		FileInputStream fis = new FileInputStream(file);
		XWPFDocument xdoc = new XWPFDocument(fis);
		XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
		str = extractor.getText();
		extractor.close();
		fis.close();

		return str;
	}

	/**
	 * 通过XWPFDocument对内容进行访问。对于XWPF文档而言，用这种方式进行读操作更佳。
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	private ArrayList<String> getRowsOfDocx(File file) throws IOException {
		ArrayList<String> docRowsList = new ArrayList<String>();
		FileInputStream is = new FileInputStream(file);
		XWPFDocument doc = new XWPFDocument(is);
		List<XWPFParagraph> paras = doc.getParagraphs();
		for (XWPFParagraph para : paras) {
			// 当前段落的属性
			// CTPPr pr = para.getCTP().getPPr();
			docRowsList.add(para.getText());
			// System.out.println(para.getText());
		}
		/*
		 * //获取文档中所有的表格 List<XWPFTable> tables = doc.getTables(); List<XWPFTableRow>
		 * rows; List<XWPFTableCell> cells;
		 * 
		 * for (XWPFTable table : tables) { //表格属性 // CTTblPr pr =
		 * table.getCTTbl().getTblPr(); //获取表格对应的行 rows = table.getRows(); for
		 * (XWPFTableRow row : rows) { //获取行对应的单元格 cells = row.getTableCells(); for
		 * (XWPFTableCell cell : cells) { System.out.println(cell.getText());; } } }
		 */
		is.close();

		return docRowsList;
	}
}
