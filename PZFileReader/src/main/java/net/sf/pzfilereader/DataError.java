/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed 
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 CONDITIONS OF ANY KIND, either express or implied. See the License for 
 the specific language governing permissions and limitations under the License.  
 */
package net.sf.pzfilereader;

/**
 * This class holds errors that occured while parsing or processing a data file.
 * 
 * @author Paul Zepernick
 * @version 2.0
 */
public class DataError {

    /** Description of error. */
    private String errorDesc = null;

    /** line number in file error occured on. */
    private int lineNo = 0;

    /**
     * Severity of the error 1 = Warning 2 = Moderate 3 = Severe.
     */
    private int errorLevel = 0;

    
    
    public DataError(String errorDesc, int lineNo, int errorLevel) {
        this.errorDesc = errorDesc;
        this.lineNo = lineNo;
        this.errorLevel = errorLevel;
    }

    /**
     * @deprecated should use the ctor with fields
     *
     */
    public DataError() {
    }

    /**
     * Returns the errorDesc.
     * 
     * @return String
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Returns the errorLevel.
     * 
     * @return int
     */
    public int getErrorLevel() {
        return errorLevel;
    }

    /**
     * Returns the lineNo.
     * 
     * @return int
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * Sets the errorDesc.
     * 
     * @param errorDesc
     *            The errorDesc to set
     * @deprecated the DataError should be immutable (i.e. no Set method) 
     */
    public void setErrorDesc(final String errorDesc) {
        this.errorDesc = errorDesc;
    }

    /**
     * Sets the errorLevel.
     * 
     * @param errorLevel
     *            The errorLevel to set
     * @deprecated the DataError should be immutable (i.e. no Set method) 
     */
    public void setErrorLevel(final int errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * Sets the lineNo.
     * 
     * @param lineNo
     *            The lineNo to set
     * @deprecated the DataError should be immutable (i.e. no Set method) 
     */
    public void setLineNo(final int lineNo) {
        this.lineNo = lineNo;
    }
}
