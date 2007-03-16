begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|demo
operator|.
name|html
operator|.
name|HTMLParser
import|;
end_import

begin_comment
comment|/**  * A DocMaker using the (compressed) Trec collection for its input.  */
end_comment

begin_class
DECL|class|TrecDocMaker
specifier|public
class|class
name|TrecDocMaker
extends|extends
name|BasicDocMaker
block|{
DECL|field|newline
specifier|private
specifier|static
specifier|final
name|String
name|newline
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|dateFormat
specifier|private
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
init|=
literal|null
decl_stmt|;
DECL|field|inputFiles
specifier|private
name|ArrayList
name|inputFiles
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|nextFile
specifier|private
name|int
name|nextFile
init|=
literal|0
decl_stmt|;
DECL|field|iteration
specifier|private
name|int
name|iteration
init|=
literal|0
decl_stmt|;
DECL|field|reader
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|zis
specifier|private
name|GZIPInputStream
name|zis
decl_stmt|;
comment|/* (non-Javadoc)    * @see SimpleDocMaker#setConfig(java.util.Properties)    */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|String
name|d
init|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.dir"
argument_list|,
literal|"trec"
argument_list|)
decl_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
literal|"work"
argument_list|)
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|collectFiles
argument_list|(
name|dataDir
argument_list|,
name|inputFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputFiles
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No txt files in dataDir: "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
comment|// date format: 30-MAR-1987 14:22:36.87
name|dateFormat
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"EEE, dd MMM yyyy kk:mm:ss "
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
comment|//Tue, 09 Dec 2003 22:39:08 GMT
name|dateFormat
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|openNextFile
specifier|private
name|void
name|openNextFile
parameter_list|()
throws|throws
name|Exception
block|{
name|closeInputs
argument_list|()
expr_stmt|;
name|int
name|retries
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|retries
operator|<
literal|20
condition|)
block|{
name|File
name|f
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|f
operator|=
operator|(
name|File
operator|)
name|inputFiles
operator|.
name|get
argument_list|(
name|nextFile
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextFile
operator|>=
name|inputFiles
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// exhausted files, start a new round
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|++
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"opening: "
operator|+
name|f
operator|+
literal|" length: "
operator|+
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|zis
operator|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|retries
operator|++
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skipping 'bad' file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"  #retries="
operator|+
name|retries
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|zis
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|closeInputs
specifier|private
name|void
name|closeInputs
parameter_list|()
block|{
if|if
condition|(
name|zis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|zis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"closeInputs(): Ingnoring error: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|zis
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"closeInputs(): Ingnoring error: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// read until finding a line that starts with the specified prefix
DECL|method|read
specifier|private
name|StringBuffer
name|read
parameter_list|(
name|String
name|prefix
parameter_list|,
name|StringBuffer
name|sb
parameter_list|,
name|boolean
name|collectMatchLine
parameter_list|,
name|boolean
name|collectAll
parameter_list|)
throws|throws
name|Exception
block|{
name|sb
operator|=
operator|(
name|sb
operator|==
literal|null
condition|?
operator|new
name|StringBuffer
argument_list|()
else|:
name|sb
operator|)
expr_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
if|if
condition|(
name|collectMatchLine
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|collectAll
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
operator|+
name|line
argument_list|)
expr_stmt|;
name|sep
operator|=
name|newline
expr_stmt|;
block|}
block|}
comment|//System.out.println("read: "+sb);
return|return
name|sb
return|;
block|}
DECL|method|getNextDocData
specifier|protected
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|openNextFile
argument_list|()
expr_stmt|;
block|}
comment|// 1. skip until doc start
name|read
argument_list|(
literal|"<DOC>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 2. name
name|StringBuffer
name|sb
init|=
name|read
argument_list|(
literal|"<DOCNO>"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|"<DOCNO>"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|indexOf
argument_list|(
literal|"</DOCNO>"
argument_list|)
argument_list|)
operator|+
literal|"_"
operator|+
name|iteration
expr_stmt|;
comment|// 3. skip until doc header
name|read
argument_list|(
literal|"<DOCHDR>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 4. date
name|sb
operator|=
name|read
argument_list|(
literal|"Date: "
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|dateStr
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|"Date: "
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// 5. skip until end of doc header
name|read
argument_list|(
literal|"</DOCHDR>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 6. collect until end of doc
name|sb
operator|=
name|read
argument_list|(
literal|"</DOC>"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// this is the next document, so parse it
comment|// TODO use a more robust html parser (current one aborts parsing quite easily).
name|HTMLParser
name|p
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// title
name|String
name|title
init|=
name|p
operator|.
name|getTitle
argument_list|()
decl_stmt|;
comment|// properties
name|Properties
name|props
init|=
name|p
operator|.
name|getMetaTags
argument_list|()
decl_stmt|;
comment|// body
name|Reader
name|r
init|=
name|p
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|char
name|c
index|[]
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|StringBuffer
name|bodyBuf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|r
operator|.
name|read
argument_list|(
name|c
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|bodyBuf
operator|.
name|append
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|addBytes
argument_list|(
name|bodyBuf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
try|try
block|{
name|dd
operator|.
name|date
operator|=
name|dateFormat
operator|.
name|parse
argument_list|(
name|dateStr
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// do not fail test just because a date could not be parsed
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ignoring date parse exception (assigning 'now') for: "
operator|+
name|dateStr
argument_list|)
expr_stmt|;
name|dd
operator|.
name|date
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
comment|// now
block|}
name|dd
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|dd
operator|.
name|title
operator|=
name|title
expr_stmt|;
name|dd
operator|.
name|body
operator|=
name|bodyBuf
operator|.
name|toString
argument_list|()
expr_stmt|;
name|dd
operator|.
name|props
operator|=
name|props
expr_stmt|;
return|return
name|dd
return|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#resetIinputs()    */
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|closeInputs
argument_list|()
expr_stmt|;
name|nextFile
operator|=
literal|0
expr_stmt|;
name|iteration
operator|=
literal|0
expr_stmt|;
block|}
comment|/*    *  (non-Javadoc)    * @see DocMaker#numUniqueTexts()    */
DECL|method|numUniqueTexts
specifier|public
name|int
name|numUniqueTexts
parameter_list|()
block|{
return|return
name|inputFiles
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

