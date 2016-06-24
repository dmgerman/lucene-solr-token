begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Loader for text files that represent a list of stopwords.  *   * @see IOUtils to obtain {@link Reader} instances  * @lucene.internal  */
end_comment

begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
DECL|field|INITIAL_CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_CAPACITY
init|=
literal|16
decl_stmt|;
comment|/** no instance */
DECL|method|WordlistLoader
specifier|private
name|WordlistLoader
parameter_list|()
block|{}
comment|/**    * Reads lines from a Reader and adds every line as an entry to a CharArraySet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @param result the {@link CharArraySet} to fill with the readers words    * @return the given {@link CharArraySet} with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|CharArraySet
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|CharArraySet
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
name|getBufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads lines from a Reader and adds every line as an entry to a CharArraySet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @return A {@link CharArraySet} with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|CharArraySet
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|INITIAL_CAPACITY
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads lines from a Reader and adds every non-comment line as an entry to a CharArraySet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @param comment The string representing a comment.    * @return A CharArraySet with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|CharArraySet
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|comment
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|comment
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|INITIAL_CAPACITY
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads lines from a Reader and adds every non-comment line as an entry to a CharArraySet (omitting    * leading and trailing whitespace). Every line of the Reader should contain only    * one word. The words need to be in lowercase if you make use of an    * Analyzer which uses LowerCaseFilter (like StandardAnalyzer).    *    * @param reader Reader containing the wordlist    * @param comment The string representing a comment.    * @param result the {@link CharArraySet} to fill with the readers words    * @return the given {@link CharArraySet} with the reader's words    */
DECL|method|getWordSet
specifier|public
specifier|static
name|CharArraySet
name|getWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|comment
parameter_list|,
name|CharArraySet
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
name|getBufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|String
name|word
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|word
operator|.
name|startsWith
argument_list|(
name|comment
argument_list|)
operator|==
literal|false
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|word
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads stopwords from a stopword list in Snowball format.    *<p>    * The snowball format is the following:    *<ul>    *<li>Lines may contain multiple words separated by whitespace.    *<li>The comment character is the vertical line (&#124;).    *<li>Lines may contain trailing comments.    *</ul>    *     * @param reader Reader containing a Snowball stopword list    * @param result the {@link CharArraySet} to fill with the readers words    * @return the given {@link CharArraySet} with the reader's words    */
DECL|method|getSnowballWordSet
specifier|public
specifier|static
name|CharArraySet
name|getSnowballWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|CharArraySet
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
name|getBufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|int
name|comment
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'|'
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|>=
literal|0
condition|)
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|String
name|words
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
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
name|words
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|words
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|add
argument_list|(
name|words
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads stopwords from a stopword list in Snowball format.    *<p>    * The snowball format is the following:    *<ul>    *<li>Lines may contain multiple words separated by whitespace.    *<li>The comment character is the vertical line (&#124;).    *<li>Lines may contain trailing comments.    *</ul>    *     * @param reader Reader containing a Snowball stopword list    * @return A {@link CharArraySet} with the reader's words    */
DECL|method|getSnowballWordSet
specifier|public
specifier|static
name|CharArraySet
name|getSnowballWordSet
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSnowballWordSet
argument_list|(
name|reader
argument_list|,
operator|new
name|CharArraySet
argument_list|(
name|INITIAL_CAPACITY
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Reads a stem dictionary. Each line contains:    *<pre>word<b>\t</b>stem</pre>    * (i.e. two tab separated words)    *    * @return stem dictionary that overrules the stemming algorithm    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getStemDict
specifier|public
specifier|static
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|getStemDict
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
literal|null
decl_stmt|;
try|try
block|{
name|br
operator|=
name|getBufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|wordstem
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|wordstem
index|[
literal|0
index|]
argument_list|,
name|wordstem
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Accesses a resource by name and returns the (non comment) lines containing    * data using the given character encoding.    *    *<p>    * A comment line is any line that starts with the character "#"    *</p>    *    * @return a list of non-blank non-comment lines with whitespace trimmed    * @throws IOException If there is a low-level I/O error.    */
DECL|method|getLines
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|input
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lines
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|input
operator|=
name|getBufferedReader
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|stream
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
name|lines
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|word
init|=
literal|null
init|;
operator|(
name|word
operator|=
name|input
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
comment|// skip initial bom marker
if|if
condition|(
name|lines
operator|.
name|isEmpty
argument_list|()
operator|&&
name|word
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|word
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'\uFEFF'
condition|)
name|word
operator|=
name|word
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// skip comments
if|if
condition|(
name|word
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
continue|continue;
name|word
operator|=
name|word
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// skip blank lines
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|lines
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|lines
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getBufferedReader
specifier|private
specifier|static
name|BufferedReader
name|getBufferedReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|(
name|reader
operator|instanceof
name|BufferedReader
operator|)
condition|?
operator|(
name|BufferedReader
operator|)
name|reader
else|:
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
end_class

end_unit
