begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
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
name|ByteArrayInputStream
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
name|InputStream
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|analysis
operator|.
name|LetterTokenizer
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
name|analysis
operator|.
name|LowerCaseFilter
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
name|analysis
operator|.
name|StopAnalyzer
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
name|analysis
operator|.
name|StopFilter
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/** Verifies that Lucene PatternAnalyzer and normal Lucene Analyzers have the same behaviour, returning the same results for any given free text. Runs a set of texts against a tokenizers/analyzers Can also be used as a simple benchmark.<p> Example usage:<pre> cd lucene-cvs java org.apache.lucene.index.memory.PatternAnalyzerTest 1 1 patluc 1 2 2 *.txt *.xml docs/*.html src/java/org/apache/lucene/index/*.java xdocs/*.xml ../nux/samples/data/*.xml</pre>  with WhitespaceAnalyzer problems can be found; These are not bugs but questionable  Lucene features: CharTokenizer.MAX_WORD_LEN = 255. Thus the PatternAnalyzer produces correct output, whereas the WhitespaceAnalyzer  silently truncates text, and so the comparison results in assertEquals() don't match up.   @author whoschek.AT.lbl.DOT.gov */
end_comment

begin_class
DECL|class|PatternAnalyzerTest
specifier|public
class|class
name|PatternAnalyzerTest
extends|extends
name|TestCase
block|{
comment|/** Runs the tests and/or benchmark */
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
throws|throws
name|Throwable
block|{
operator|new
name|PatternAnalyzerTest
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|testMany
specifier|public
name|void
name|testMany
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//    String[] files = MemoryIndexTest.listFiles(new String[] {
comment|//      "*.txt", "*.html", "*.xml", "xdocs/*.xml",
comment|//      "src/test/org/apache/lucene/queryParser/*.java",
comment|//      "src/org/apache/lucene/index/memory/*.java",
comment|//    });
name|String
index|[]
name|files
init|=
name|MemoryIndexTest
operator|.
name|listFiles
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"../../*.txt"
block|,
literal|"../../*.html"
block|,
literal|"../../*.xml"
block|,
literal|"../../xdocs/*.xml"
block|,
literal|"../../src/test/org/apache/lucene/queryParser/*.java"
block|,
literal|"src/java/org/apache/lucene/index/memory/*.java"
block|,     }
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"files = "
operator|+
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
argument_list|(
name|files
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|xargs
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"1"
block|,
literal|"patluc"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"2"
block|,     }
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
name|xargs
operator|.
name|length
operator|+
name|files
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|xargs
argument_list|,
literal|0
argument_list|,
name|args
argument_list|,
literal|0
argument_list|,
name|xargs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|files
argument_list|,
literal|0
argument_list|,
name|args
argument_list|,
name|xargs
operator|.
name|length
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|private
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|int
name|k
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|iters
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|iters
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|runs
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|runs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cmd
init|=
literal|"patluc"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|cmd
operator|=
name|args
index|[
name|k
index|]
expr_stmt|;
name|boolean
name|usePattern
init|=
name|cmd
operator|.
name|indexOf
argument_list|(
literal|"pat"
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|useLucene
init|=
name|cmd
operator|.
name|indexOf
argument_list|(
literal|"luc"
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|int
name|maxLetters
init|=
literal|1
decl_stmt|;
comment|// = 2: CharTokenizer.MAX_WORD_LEN issue; see class javadoc
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|maxLetters
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
expr_stmt|;
name|int
name|maxToLower
init|=
literal|2
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|maxToLower
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
expr_stmt|;
name|int
name|maxStops
init|=
literal|2
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|maxStops
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
literal|"CHANGES.txt"
argument_list|)
block|,
operator|new
name|File
argument_list|(
literal|"LICENSE.txt"
argument_list|)
block|}
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
block|{
name|files
operator|=
operator|new
name|File
index|[
name|args
operator|.
name|length
operator|-
name|k
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|k
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
operator|-
name|k
index|]
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n########### iteration="
operator|+
name|iter
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|bytes
init|=
literal|0
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
name|files
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
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
continue|continue;
comment|// ignore
name|bytes
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
name|String
name|text
init|=
name|toString
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n*********** FILE="
operator|+
name|file
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|letters
init|=
literal|0
init|;
name|letters
operator|<
name|maxLetters
condition|;
name|letters
operator|++
control|)
block|{
name|boolean
name|lettersOnly
init|=
name|letters
operator|==
literal|0
decl_stmt|;
for|for
control|(
name|int
name|stops
init|=
literal|0
init|;
name|stops
operator|<
name|maxStops
condition|;
name|stops
operator|++
control|)
block|{
name|Set
name|stopWords
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stops
operator|!=
literal|0
condition|)
name|stopWords
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|toLower
init|=
literal|0
init|;
name|toLower
operator|<
name|maxToLower
condition|;
name|toLower
operator|++
control|)
block|{
name|boolean
name|toLowerCase
init|=
name|toLower
operator|!=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|run
init|=
literal|0
init|;
name|run
operator|<
name|runs
condition|;
name|run
operator|++
control|)
block|{
name|List
name|tokens1
init|=
literal|null
decl_stmt|;
name|List
name|tokens2
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|usePattern
condition|)
name|tokens1
operator|=
name|getTokens
argument_list|(
name|patternTokenStream
argument_list|(
name|text
argument_list|,
name|lettersOnly
argument_list|,
name|toLowerCase
argument_list|,
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|useLucene
condition|)
name|tokens2
operator|=
name|getTokens
argument_list|(
name|luceneTokenStream
argument_list|(
name|text
argument_list|,
name|lettersOnly
argument_list|,
name|toLowerCase
argument_list|,
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|usePattern
operator|&&
name|useLucene
condition|)
name|assertEquals
argument_list|(
name|tokens1
argument_list|,
name|tokens2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fatal error at file="
operator|+
name|file
operator|+
literal|", letters="
operator|+
name|lettersOnly
operator|+
literal|", toLowerCase="
operator|+
name|toLowerCase
operator|+
literal|", stopwords="
operator|+
operator|(
name|stopWords
operator|!=
literal|null
condition|?
literal|"english"
else|:
literal|"none"
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\ntokens1="
operator|+
name|toString
argument_list|(
name|tokens1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\ntokens2="
operator|+
name|toString
argument_list|(
name|tokens2
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nsecs = "
operator|+
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"files/sec= "
operator|+
operator|(
literal|1.0f
operator|*
name|runs
operator|*
name|maxLetters
operator|*
name|maxToLower
operator|*
name|maxStops
operator|*
name|files
operator|.
name|length
operator|/
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
operator|)
argument_list|)
expr_stmt|;
name|float
name|mb
init|=
operator|(
literal|1.0f
operator|*
name|bytes
operator|*
name|runs
operator|*
name|maxLetters
operator|*
name|maxToLower
operator|*
name|maxStops
operator|)
operator|/
operator|(
literal|1024.0f
operator|*
literal|1024.0f
operator|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MB/sec = "
operator|+
operator|(
name|mb
operator|/
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|usePattern
operator|&&
name|useLucene
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No bug found. done."
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done benchmarking (without checking correctness)."
argument_list|)
expr_stmt|;
block|}
DECL|method|patternTokenStream
specifier|private
name|TokenStream
name|patternTokenStream
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|toLowerCase
parameter_list|,
name|Set
name|stopWords
parameter_list|)
block|{
name|Pattern
name|pattern
decl_stmt|;
if|if
condition|(
name|letters
condition|)
name|pattern
operator|=
name|PatternAnalyzer
operator|.
name|NON_WORD_PATTERN
expr_stmt|;
else|else
name|pattern
operator|=
name|PatternAnalyzer
operator|.
name|WHITESPACE_PATTERN
expr_stmt|;
name|PatternAnalyzer
name|analyzer
init|=
operator|new
name|PatternAnalyzer
argument_list|(
name|pattern
argument_list|,
name|toLowerCase
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|text
argument_list|)
return|;
block|}
DECL|method|luceneTokenStream
specifier|private
name|TokenStream
name|luceneTokenStream
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|toLowerCase
parameter_list|,
name|Set
name|stopWords
parameter_list|)
block|{
name|TokenStream
name|stream
decl_stmt|;
if|if
condition|(
name|letters
condition|)
name|stream
operator|=
operator|new
name|LetterTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|stream
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|toLowerCase
condition|)
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopWords
operator|!=
literal|null
condition|)
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
DECL|method|getTokens
specifier|private
name|List
name|getTokens
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
name|tokens
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
name|tokens
return|;
block|}
DECL|method|assertEquals
specifier|private
name|void
name|assertEquals
parameter_list|(
name|List
name|tokens1
parameter_list|,
name|List
name|tokens2
parameter_list|)
block|{
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|tokens1
operator|.
name|size
argument_list|()
argument_list|,
name|tokens2
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|t1
init|=
operator|(
name|Token
operator|)
name|tokens1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Token
name|t2
init|=
operator|(
name|Token
operator|)
name|tokens2
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|t1
operator|.
name|termText
argument_list|()
operator|.
name|equals
argument_list|(
name|t2
operator|.
name|termText
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"termText"
argument_list|)
throw|;
if|if
condition|(
name|t1
operator|.
name|startOffset
argument_list|()
operator|!=
name|t2
operator|.
name|startOffset
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"startOffset"
argument_list|)
throw|;
if|if
condition|(
name|t1
operator|.
name|endOffset
argument_list|()
operator|!=
name|t2
operator|.
name|endOffset
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"endOffset"
argument_list|)
throw|;
if|if
condition|(
operator|!
operator|(
name|t1
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|t2
operator|.
name|type
argument_list|()
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"type"
argument_list|)
throw|;
block|}
if|if
condition|(
name|tokens1
operator|.
name|size
argument_list|()
operator|!=
name|tokens2
operator|.
name|size
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"size1="
operator|+
name|tokens1
operator|.
name|size
argument_list|()
operator|+
literal|", size2="
operator|+
name|tokens2
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"i="
operator|+
name|i
operator|+
literal|", size="
operator|+
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"t1[size]='"
operator|+
operator|(
operator|(
name|Token
operator|)
name|tokens1
operator|.
name|get
argument_list|(
name|size
operator|-
literal|1
argument_list|)
operator|)
operator|.
name|termText
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"t2[size]='"
operator|+
operator|(
operator|(
name|Token
operator|)
name|tokens2
operator|.
name|get
argument_list|(
name|size
operator|-
literal|1
argument_list|)
operator|)
operator|.
name|termText
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|toString
specifier|private
name|String
name|toString
parameter_list|(
name|List
name|tokens
parameter_list|)
block|{
if|if
condition|(
name|tokens
operator|==
literal|null
condition|)
return|return
literal|"null"
return|;
name|String
name|str
init|=
literal|"["
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
name|tokens
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|t1
init|=
operator|(
name|Token
operator|)
name|tokens
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|str
operator|=
name|str
operator|+
literal|"'"
operator|+
name|t1
operator|.
name|termText
argument_list|()
operator|+
literal|"', "
expr_stmt|;
block|}
return|return
name|str
operator|+
literal|"]"
return|;
block|}
comment|// trick to detect default platform charset
DECL|field|DEFAULT_PLATFORM_CHARSET
specifier|private
specifier|static
specifier|final
name|Charset
name|DEFAULT_PLATFORM_CHARSET
init|=
name|Charset
operator|.
name|forName
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|getEncoding
argument_list|()
argument_list|)
decl_stmt|;
comment|// the following utility methods below are copied from Apache style Nux library - see http://dsd.lbl.gov/nux
DECL|method|toString
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
name|charset
operator|=
name|DEFAULT_PLATFORM_CHARSET
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|toByteArray
argument_list|(
name|input
argument_list|)
decl_stmt|;
return|return
name|charset
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toByteArray
specifier|private
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// safe and fast even if input.available() behaves weird or buggy
name|int
name|len
init|=
name|Math
operator|.
name|max
argument_list|(
literal|256
argument_list|,
name|input
operator|.
name|available
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|byte
index|[]
name|output
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|len
operator|+
name|n
operator|>
name|output
operator|.
name|length
condition|)
block|{
comment|// grow capacity
name|byte
name|tmp
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|output
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|len
operator|+
name|n
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|output
expr_stmt|;
comment|// use larger buffer for future larger bulk reads
name|output
operator|=
name|tmp
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|len
operator|+=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|==
name|output
operator|.
name|length
condition|)
return|return
name|output
return|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// help gc
name|buffer
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

