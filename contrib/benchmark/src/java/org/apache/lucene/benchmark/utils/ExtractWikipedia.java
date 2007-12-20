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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|XMLReaderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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

begin_comment
comment|/**  * Extract the downloaded Wikipedia dump into separate files for indexing.  */
end_comment

begin_class
DECL|class|ExtractWikipedia
specifier|public
class|class
name|ExtractWikipedia
block|{
DECL|field|wikipedia
specifier|private
name|File
name|wikipedia
decl_stmt|;
DECL|field|outputDir
specifier|private
name|File
name|outputDir
decl_stmt|;
DECL|method|ExtractWikipedia
specifier|public
name|ExtractWikipedia
parameter_list|(
name|File
name|wikipedia
parameter_list|,
name|File
name|outputDir
parameter_list|)
block|{
name|this
operator|.
name|wikipedia
operator|=
name|wikipedia
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
name|File
index|[]
name|files
init|=
name|outputDir
operator|.
name|listFiles
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
name|files
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
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|count
specifier|static
specifier|public
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|months
specifier|static
name|String
index|[]
name|months
init|=
block|{
literal|"JAN"
block|,
literal|"FEB"
block|,
literal|"MAR"
block|,
literal|"APR"
block|,
literal|"MAY"
block|,
literal|"JUN"
block|,
literal|"JUL"
block|,
literal|"AUG"
block|,
literal|"SEP"
block|,
literal|"OCT"
block|,
literal|"NOV"
block|,
literal|"DEC"
block|}
decl_stmt|;
DECL|class|Parser
specifier|public
class|class
name|Parser
extends|extends
name|DefaultHandler
block|{
DECL|method|Parser
specifier|public
name|Parser
parameter_list|()
block|{     }
DECL|field|contents
name|StringBuffer
name|contents
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|contents
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|field|title
name|String
name|title
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|body
name|String
name|body
decl_stmt|;
DECL|field|time
name|String
name|time
decl_stmt|;
DECL|field|BASE
specifier|static
specifier|final
name|int
name|BASE
init|=
literal|10
decl_stmt|;
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"page"
argument_list|)
condition|)
block|{
name|title
operator|=
literal|null
expr_stmt|;
name|id
operator|=
literal|null
expr_stmt|;
name|body
operator|=
literal|null
expr_stmt|;
name|time
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|directory
specifier|public
name|File
name|directory
parameter_list|(
name|int
name|count
parameter_list|,
name|File
name|directory
parameter_list|)
block|{
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
name|outputDir
expr_stmt|;
block|}
name|int
name|base
init|=
name|BASE
decl_stmt|;
while|while
condition|(
name|base
operator|<=
name|count
condition|)
block|{
name|base
operator|*=
name|BASE
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|<
name|BASE
condition|)
block|{
return|return
name|directory
return|;
block|}
name|directory
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
operator|(
name|Integer
operator|.
name|toString
argument_list|(
name|base
operator|/
name|BASE
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|directory
operator|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
operator|(
name|Integer
operator|.
name|toString
argument_list|(
name|count
operator|/
operator|(
name|base
operator|/
name|BASE
operator|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
return|return
name|directory
argument_list|(
name|count
operator|%
operator|(
name|base
operator|/
name|BASE
operator|)
argument_list|,
name|directory
argument_list|)
return|;
block|}
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|title
parameter_list|,
name|String
name|time
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|File
name|d
init|=
name|directory
argument_list|(
name|count
operator|++
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|d
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|d
argument_list|,
name|id
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
name|StringBuffer
name|contents
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|time
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|contents
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|FileWriter
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|time
name|String
name|time
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|8
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|months
index|[
name|Integer
operator|.
name|valueOf
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|11
argument_list|,
literal|19
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|".000"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|)
block|{
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|title
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"text"
argument_list|)
condition|)
block|{
name|body
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|body
operator|.
name|startsWith
argument_list|(
literal|"#REDIRECT"
argument_list|)
operator|||
name|body
operator|.
name|startsWith
argument_list|(
literal|"#redirect"
argument_list|)
condition|)
block|{
name|body
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|time
operator|=
name|time
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
operator|&&
name|id
operator|==
literal|null
condition|)
block|{
name|id
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qualified
operator|.
name|equals
argument_list|(
literal|"page"
argument_list|)
condition|)
block|{
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|create
argument_list|(
name|id
argument_list|,
name|title
argument_list|,
name|time
argument_list|,
name|body
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|extract
specifier|public
name|void
name|extract
parameter_list|()
block|{
try|try
block|{
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|()
decl_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|SAXParser
name|sp
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|sp
operator|.
name|parse
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|wikipedia
argument_list|)
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XMLReader
name|reader
init|=
name|XMLReaderFactory
operator|.
name|createXMLReader
argument_list|(
literal|"org.apache.xerces.parsers.SAXParser"
argument_list|)
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setErrorHandler
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|wikipedia
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
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
name|printUsage
argument_list|()
expr_stmt|;
block|}
name|File
name|wikipedia
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
name|wikipedia
operator|.
name|exists
argument_list|()
condition|)
block|{
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
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ExtractWikipedia
name|extractor
init|=
operator|new
name|ExtractWikipedia
argument_list|(
name|wikipedia
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|extract
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|printUsage
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printUsage
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java -cp<...> org.apache.lucene.benchmark.utils.ExtractWikipedia<Path to Wikipedia XML file><Output Path>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

begin_escape
end_escape

end_unit

