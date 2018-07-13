package com.idoc.pdf;
import java.io.IOException;


public interface Loader {
	ResponseBean load(String path, String loadCurrency) throws IOException ;
	String descrition() ;
}
