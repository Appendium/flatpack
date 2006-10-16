/*
 Copyright 2006 Paul Zepernick

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.  
 */
package com.pz.reader;


/**
 * This class holds errors that occured while parsing or processing a data file.
 * @author Paul Zepernick
 * @version 2.0
 */
public class DataError {
 
    /** Description of error */
    private String            errorDesc        = null;
    /** line number in file error occured on */
    private int               lineNo           = 0;
    /**
     * Severity of the error 1 = Warning 2 = Moderate 3 = Severe
     */
    private int               errorLevel       = 0;

    /** default constructor */
    public DataError() {
        super();
    }

    /**
     * Returns the errorDesc.
     * @return String
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Returns the errorLevel.
     * @return int
     */
    public int getErrorLevel() {
        return errorLevel;
    }

    /**
     * Returns the lineNo.
     * @return int
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * Sets the errorDesc.
     * @param errorDesc The errorDesc to set
     */
    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    /**
     * Sets the errorLevel.
     * @param errorLevel The errorLevel to set
     */
    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * Sets the lineNo.
     * @param lineNo The lineNo to set
     */
    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

}
