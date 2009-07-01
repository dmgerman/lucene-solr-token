begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Configure analysis data for SmartChineseAnalyzer  *<p>  * SmartChineseAnalyzer has a built-in dictionary and stopword list out-of-box.  *</p>  *<p>  * In special circumstances a user may wish to configure SmartChineseAnalyzer with a custom data directory location.  *</p>  * AnalyzerProfile is used to determine the location of the data directory containing bigramdict.dct and coredict.dct.  * The following order is used to determine the location of the data directory:  *   *<ol>  *<li>System propertyï¼ -Danalysis.data.dir=/path/to/analysis-data</li>  *<li>Relative path: analysis-data</li>  *<li>Relative path: lib/analysis-data</li>  *<li>Property file: analysis.data.dir property from relative path analysis.properties</li>  *<li>Property file: analysis.data.dir property from relative path lib/analysis.properties</li>  *</ol>  *   * Example property fileï¼  *   *<pre>  * analysis.data.dir=D:/path/to/analysis-data/  *</pre>  *   *   */
end_comment

begin_class
DECL|class|AnalyzerProfile
specifier|public
class|class
name|AnalyzerProfile
block|{
comment|/**    * Global indicating the configured analysis data directory    */
DECL|field|ANALYSIS_DATA_DIR
specifier|public
specifier|static
name|String
name|ANALYSIS_DATA_DIR
init|=
literal|""
decl_stmt|;
static|static
block|{
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|private
specifier|static
name|void
name|init
parameter_list|()
block|{
name|String
name|dirName
init|=
literal|"analysis-data"
decl_stmt|;
name|String
name|propName
init|=
literal|"analysis.properties"
decl_stmt|;
comment|// Try the system propertyï¼-Danalysis.data.dir=/path/to/analysis-data
name|ANALYSIS_DATA_DIR
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"analysis.data.dir"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|ANALYSIS_DATA_DIR
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
return|return;
name|File
index|[]
name|cadidateFiles
init|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
literal|"./"
operator|+
name|dirName
argument_list|)
block|,
operator|new
name|File
argument_list|(
literal|"./lib/"
operator|+
name|dirName
argument_list|)
block|,
operator|new
name|File
argument_list|(
literal|"./"
operator|+
name|propName
argument_list|)
block|,
operator|new
name|File
argument_list|(
literal|"./lib/"
operator|+
name|propName
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cadidateFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|cadidateFiles
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|ANALYSIS_DATA_DIR
operator|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
operator|&&
name|getAnalysisDataDir
argument_list|(
name|file
argument_list|)
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|ANALYSIS_DATA_DIR
operator|=
name|getAnalysisDataDir
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|ANALYSIS_DATA_DIR
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Dictionary directory cannot be found.
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: Can not find lexical dictionary directory!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: This will cause unpredictable exceptions in your application!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: Please refer to the manual to download the dictionaries."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAnalysisDataDir
specifier|private
specifier|static
name|String
name|getAnalysisDataDir
parameter_list|(
name|File
name|propFile
parameter_list|)
block|{
name|Properties
name|prop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|FileInputStream
name|input
init|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
name|prop
operator|.
name|load
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|String
name|dir
init|=
name|prop
operator|.
name|getProperty
argument_list|(
literal|"analysis.data.dir"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

