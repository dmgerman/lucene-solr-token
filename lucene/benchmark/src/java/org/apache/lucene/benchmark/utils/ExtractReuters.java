begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FileFilter
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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * Split the Reuters SGML documents into Simple Text files containing: Title, Date, Dateline, Body  */
end_comment

begin_class
DECL|class|ExtractReuters
specifier|public
class|class
name|ExtractReuters
block|{
DECL|field|reutersDir
specifier|private
name|File
name|reutersDir
decl_stmt|;
DECL|field|outputDir
specifier|private
name|File
name|outputDir
decl_stmt|;
DECL|field|LINE_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|LINE_SEPARATOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|method|ExtractReuters
specifier|public
name|ExtractReuters
parameter_list|(
name|File
name|reutersDir
parameter_list|,
name|File
name|outputDir
parameter_list|)
block|{
name|this
operator|.
name|reutersDir
operator|=
name|reutersDir
expr_stmt|;
name|this
operator|.
name|outputDir
operator|=
name|outputDir
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleting all files in "
operator|+
name|outputDir
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|outputDir
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|extract
specifier|public
name|void
name|extract
parameter_list|()
block|{
name|File
index|[]
name|sgmFiles
init|=
name|reutersDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".sgm"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|sgmFiles
operator|!=
literal|null
operator|&&
name|sgmFiles
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|File
name|sgmFile
range|:
name|sgmFiles
control|)
block|{
name|extractFile
argument_list|(
name|sgmFile
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"No .sgm files in "
operator|+
name|reutersDir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|EXTRACTION_PATTERN
name|Pattern
name|EXTRACTION_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<TITLE>(.*?)</TITLE>|<DATE>(.*?)</DATE>|<BODY>(.*?)</BODY>"
argument_list|)
decl_stmt|;
DECL|field|META_CHARS
specifier|private
specifier|static
name|String
index|[]
name|META_CHARS
init|=
block|{
literal|"&"
block|,
literal|"<"
block|,
literal|">"
block|,
literal|"\""
block|,
literal|"'"
block|}
decl_stmt|;
DECL|field|META_CHARS_SERIALIZATIONS
specifier|private
specifier|static
name|String
index|[]
name|META_CHARS_SERIALIZATIONS
init|=
block|{
literal|"&amp;"
block|,
literal|"&lt;"
block|,
literal|"&gt;"
block|,
literal|"&quot;"
block|,
literal|"&apos;"
block|}
decl_stmt|;
comment|/**    * Override if you wish to change what is extracted    *     * @param sgmFile    */
DECL|method|extractFile
specifier|protected
name|void
name|extractFile
parameter_list|(
name|File
name|sgmFile
parameter_list|)
block|{
try|try
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|sgmFile
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|StringBuilder
name|outBuffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|int
name|docNumber
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// when we see a closing reuters tag, flush the file
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"</REUTERS"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Replace the SGM escape sequences
name|buffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// accumulate the strings for now,
comment|// then apply regular expression to
comment|// get the pieces,
block|}
else|else
block|{
comment|// Extract the relevant pieces and write to a file in the output dir
name|Matcher
name|matcher
init|=
name|EXTRACTION_PATTERN
operator|.
name|matcher
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|matcher
operator|.
name|groupCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|matcher
operator|.
name|group
argument_list|(
name|i
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|outBuffer
operator|.
name|append
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|outBuffer
operator|.
name|append
argument_list|(
name|LINE_SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|LINE_SEPARATOR
argument_list|)
expr_stmt|;
block|}
name|String
name|out
init|=
name|outBuffer
operator|.
name|toString
argument_list|()
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
name|META_CHARS_SERIALIZATIONS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|=
name|out
operator|.
name|replaceAll
argument_list|(
name|META_CHARS_SERIALIZATIONS
index|[
name|i
index|]
argument_list|,
name|META_CHARS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|outputDir
argument_list|,
name|sgmFile
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
operator|(
name|docNumber
operator|++
operator|)
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
comment|// System.out.println("Writing " + outFile);
name|OutputStreamWriter
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outFile
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|outBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|usage
argument_list|(
literal|"Wrong number of arguments ("
operator|+
name|args
operator|.
name|length
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return;
block|}
name|File
name|reutersDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|reutersDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|usage
argument_list|(
literal|"Cannot find Path to Reuters SGM files ("
operator|+
name|reutersDir
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// First, extract to a tmp directory and only if everything succeeds, rename
comment|// to output directory.
name|File
name|outputDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|outputDir
operator|=
operator|new
name|File
argument_list|(
name|outputDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"-tmp"
argument_list|)
expr_stmt|;
name|outputDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ExtractReuters
name|extractor
init|=
operator|new
name|ExtractReuters
argument_list|(
name|reutersDir
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|extract
argument_list|()
expr_stmt|;
comment|// Now rename to requested output dir
name|outputDir
operator|.
name|renameTo
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|msg
operator|+
literal|" :: java -cp<...> org.apache.lucene.benchmark.utils.ExtractReuters<Path to Reuters SGM files><Output Path>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

