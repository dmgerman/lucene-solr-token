begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|core
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
name|util
operator|.
name|IOUtils
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
name|Version
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
name|CharsetDecoder
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
name|CodingErrorAction
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
name|Collections
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_comment
comment|/**  * Abstract parent class for analysis factories {@link TokenizerFactory},  * {@link TokenFilterFactory} and {@link CharFilterFactory}.  *<p>  * The typical lifecycle for a factory consumer is:  *<ol>  *<li>Create factory via its a no-arg constructor  *<li>Set version emulation by calling {@link #setLuceneMatchVersion(Version)}  *<li>Calls {@link #init(Map)} passing arguments as key-value mappings.  *<li>(Optional) If the factory uses resources such as files, {@link ResourceLoaderAware#inform(ResourceLoader)} is called to initialize those resources.  *<li>Consumer calls create() to obtain instances.  *</ol>  */
end_comment

begin_class
DECL|class|AbstractAnalysisFactory
specifier|public
specifier|abstract
class|class
name|AbstractAnalysisFactory
block|{
comment|/** The init args */
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
comment|/** the luceneVersion arg */
DECL|field|luceneMatchVersion
specifier|protected
name|Version
name|luceneMatchVersion
init|=
literal|null
decl_stmt|;
comment|/**    * Initialize this factory via a set of key-value pairs.    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|method|getArgs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getArgs
parameter_list|()
block|{
return|return
name|args
return|;
block|}
comment|/** this method can be called in the {@link org.apache.lucene.analysis.util.TokenizerFactory#create(java.io.Reader)}    * or {@link org.apache.lucene.analysis.util.TokenFilterFactory#create(org.apache.lucene.analysis.TokenStream)} methods,    * to inform user, that for this factory a {@link #luceneMatchVersion} is required */
DECL|method|assureMatchVersion
specifier|protected
specifier|final
name|void
name|assureMatchVersion
parameter_list|()
block|{
if|if
condition|(
name|luceneMatchVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: Factory '"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' needs a 'luceneMatchVersion' parameter"
argument_list|)
throw|;
block|}
block|}
DECL|method|setLuceneMatchVersion
specifier|public
name|void
name|setLuceneMatchVersion
parameter_list|(
name|Version
name|luceneMatchVersion
parameter_list|)
block|{
name|this
operator|.
name|luceneMatchVersion
operator|=
name|luceneMatchVersion
expr_stmt|;
block|}
DECL|method|getLuceneMatchVersion
specifier|public
name|Version
name|getLuceneMatchVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|luceneMatchVersion
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useDefault
condition|)
block|{
return|return
name|defaultVal
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|defaultVal
parameter_list|)
block|{
return|return
name|getBoolean
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|defaultVal
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useDefault
condition|)
return|return
name|defaultVal
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/**    * Compiles a pattern for the value of the specified argument key<code>name</code>     */
DECL|method|getPattern
specifier|protected
name|Pattern
name|getPattern
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|String
name|pat
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|pat
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Configuration Error: '"
operator|+
name|name
operator|+
literal|"' can not be parsed in "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns as {@link CharArraySet} from wordFiles, which    * can be a comma-separated list of filenames    */
DECL|method|getWordSet
specifier|protected
name|CharArraySet
name|getWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|getLines
argument_list|(
name|loader
argument_list|,
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|words
operator|.
name|addAll
argument_list|(
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|wlist
argument_list|,
name|ignoreCase
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|words
return|;
block|}
comment|/**    * Returns the resource's lines (with content treated as UTF-8)    */
DECL|method|getLines
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|WordlistLoader
operator|.
name|getLines
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|resource
argument_list|)
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
return|;
block|}
comment|/** same as {@link #getWordSet(ResourceLoader, String, boolean)},    * except the input is in snowball format. */
DECL|method|getSnowballWordSet
specifier|protected
name|CharArraySet
name|getSnowballWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|reader
argument_list|,
name|words
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|words
return|;
block|}
comment|/**    * Splits file names separated by comma character.    * File names can contain comma characters escaped by backslash '\'    *    * @param fileNames the string containing file names    * @return a list of file names with the escaping backslashed removed    */
DECL|method|splitFileNames
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|splitFileNames
parameter_list|(
name|String
name|fileNames
parameter_list|)
block|{
if|if
condition|(
name|fileNames
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
return|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|fileNames
operator|.
name|split
argument_list|(
literal|"(?<!\\\\),"
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|file
operator|.
name|replaceAll
argument_list|(
literal|"\\\\(?=,)"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

