package org.jim.doctools.merge;

public class App {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DocRepetitionRate d=new DocRepetitionRate();
		System.out.println(d.RepetitionPercentByLine(args[0],args[1]));
		System.out.println(d.RepetitionPercentByByte(args[0],args[1]));
	}

}
