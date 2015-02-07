begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package

begin_comment
comment|/*  * Copyright 2001-2005 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileOutputStream
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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|StandardCharsets
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_comment
comment|/**  * Generates a file containing JFlex macros to accept valid ASCII TLDs   * (top level domains), for inclusion in JFlex grammars that can accept   * domain names.  *<p>   * The IANA Root Zone Database is queried via HTTP from URL cmdline arg #0, the  * response is parsed, and the results are written out to a file containing   * a JFlex macro that will accept all valid ASCII-only TLDs, including punycode   * forms of internationalized TLDs (output file cmdline arg #1).  */
end_comment

begin_class
DECL|class|GenerateJflexTLDMacros
specifier|public
class|class
name|GenerateJflexTLDMacros
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"--help"
argument_list|)
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-help"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Cmd line params:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\tjava "
operator|+
name|GenerateJflexTLDMacros
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"<ZoneFileURL><JFlexOutputFile>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
operator|new
name|GenerateJflexTLDMacros
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
DECL|field|NL
specifier|private
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
DECL|field|APACHE_LICENSE
specifier|private
specifier|static
specifier|final
name|String
name|APACHE_LICENSE
init|=
literal|"/*"
operator|+
name|NL
operator|+
literal|" * Licensed to the Apache Software Foundation (ASF) under one or more"
operator|+
name|NL
operator|+
literal|" * contributor license agreements.  See the NOTICE file distributed with"
operator|+
name|NL
operator|+
literal|" * this work for additional information regarding copyright ownership."
operator|+
name|NL
operator|+
literal|" * The ASF licenses this file to You under the Apache License, Version 2.0"
operator|+
name|NL
operator|+
literal|" * (the \"License\"); you may not use this file except in compliance with"
operator|+
name|NL
operator|+
literal|" * the License.  You may obtain a copy of the License at"
operator|+
name|NL
operator|+
literal|" *"
operator|+
name|NL
operator|+
literal|" *     http://www.apache.org/licenses/LICENSE-2.0"
operator|+
name|NL
operator|+
literal|" *"
operator|+
name|NL
operator|+
literal|" * Unless required by applicable law or agreed to in writing, software"
operator|+
name|NL
operator|+
literal|" * distributed under the License is distributed on an \"AS IS\" BASIS,"
operator|+
name|NL
operator|+
literal|" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
operator|+
name|NL
operator|+
literal|" * See the License for the specific language governing permissions and"
operator|+
name|NL
operator|+
literal|" * limitations under the License."
operator|+
name|NL
operator|+
literal|" */"
operator|+
name|NL
decl_stmt|;
DECL|field|TLD_PATTERN_1
specifier|private
specifier|static
specifier|final
name|Pattern
name|TLD_PATTERN_1
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([-A-Za-z0-9]+)\\.\\s+NS\\s+.*"
argument_list|)
decl_stmt|;
DECL|field|TLD_PATTERN_2
specifier|private
specifier|static
specifier|final
name|Pattern
name|TLD_PATTERN_2
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([-A-Za-z0-9]+)\\.\\s+\\d+\\s+IN\\s+NS\\s+.*"
argument_list|)
decl_stmt|;
DECL|field|tldFileURL
specifier|private
specifier|final
name|URL
name|tldFileURL
decl_stmt|;
DECL|field|tldFileLastModified
specifier|private
name|long
name|tldFileLastModified
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|outputFile
specifier|private
specifier|final
name|File
name|outputFile
decl_stmt|;
DECL|method|GenerateJflexTLDMacros
specifier|public
name|GenerateJflexTLDMacros
parameter_list|(
name|String
name|tldFileURL
parameter_list|,
name|String
name|outputFile
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|tldFileURL
operator|=
operator|new
name|URL
argument_list|(
name|tldFileURL
argument_list|)
expr_stmt|;
name|this
operator|.
name|outputFile
operator|=
operator|new
name|File
argument_list|(
name|outputFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * Downloads the IANA Root Zone Database, extracts the ASCII TLDs, then    * writes a JFlex macro accepting any of them case-insensitively out to    * the specified output file.    *     * @throws IOException if there is a problem either downloading the database    *  or writing out the output file.    */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|TLDs
init|=
name|getIANARootZoneDatabase
argument_list|()
decl_stmt|;
name|writeOutput
argument_list|(
name|TLDs
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Wrote "
operator|+
name|TLDs
operator|.
name|size
argument_list|()
operator|+
literal|" top level domains to '"
operator|+
name|outputFile
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Downloads the IANA Root Zone Database.    * @return downcased sorted set of ASCII TLDs    * @throws java.io.IOException if there is a problem downloading the database     */
DECL|method|getIANARootZoneDatabase
specifier|private
name|SortedSet
argument_list|<
name|String
argument_list|>
name|getIANARootZoneDatabase
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|TLDs
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|URLConnection
name|connection
init|=
name|tldFileURL
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCaches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addRequestProperty
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|tldFileLastModified
operator|=
name|connection
operator|.
name|getLastModified
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
condition|)
block|{
name|Matcher
name|matcher
init|=
name|TLD_PATTERN_1
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|TLDs
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|=
name|TLD_PATTERN_2
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|TLDs
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|TLDs
return|;
block|}
comment|/**    * Writes a file containing a JFlex macro that will accept any of the given    * TLDs case-insensitively.    *     * @param ASCIITLDs The downcased sorted set of top level domains to accept    * @throws IOException if there is an error writing the output file    */
DECL|method|writeOutput
specifier|private
name|void
name|writeOutput
parameter_list|(
name|SortedSet
argument_list|<
name|String
argument_list|>
name|ASCIITLDs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DateFormat
name|dateFormat
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|(
name|DateFormat
operator|.
name|FULL
argument_list|,
name|DateFormat
operator|.
name|FULL
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|dateFormat
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
name|APACHE_LICENSE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"// Generated from IANA Root Zone Database<"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|tldFileURL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
if|if
condition|(
name|tldFileLastModified
operator|>
literal|0L
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"// file version from "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
name|tldFileLastModified
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"// generated on "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"// by "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"ASCIITLD = \".\" ("
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|boolean
name|isFirst
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|ASCIITLD
range|:
name|ASCIITLDs
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFirst
condition|)
block|{
name|isFirst
operator|=
literal|false
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"| "
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
name|getCaseInsensitiveRegex
argument_list|(
name|ASCIITLD
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"\t) \".\"?   // Accept trailing root (empty) domain"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a regex that will accept the given ASCII TLD case-insensitively.    *     * @param ASCIITLD The ASCII TLD to generate a regex for    * @return a regex that will accept the given ASCII TLD case-insensitively    */
DECL|method|getCaseInsensitiveRegex
specifier|private
name|String
name|getCaseInsensitiveRegex
parameter_list|(
name|String
name|ASCIITLD
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|ASCIITLD
operator|.
name|length
argument_list|()
condition|;
operator|++
name|pos
control|)
block|{
name|char
name|ch
init|=
name|ASCIITLD
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|'-'
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|ch
argument_list|)
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|ch
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

