begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.custom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|custom
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|AnalysisSPILoader
operator|.
name|newFactoryClassInstance
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Arrays
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
name|HashMap
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
name|Objects
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
name|Analyzer
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
name|Tokenizer
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
name|util
operator|.
name|AbstractAnalysisFactory
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
name|util
operator|.
name|CharFilterFactory
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
name|util
operator|.
name|ClasspathResourceLoader
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
name|util
operator|.
name|FilesystemResourceLoader
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
name|util
operator|.
name|ResourceLoader
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
name|util
operator|.
name|ResourceLoaderAware
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
name|util
operator|.
name|TokenFilterFactory
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
name|util
operator|.
name|TokenizerFactory
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
name|SetOnce
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

begin_comment
comment|/**  * A general-purpose Analyzer that can be created with a builder-style API.  * Under the hood it uses the factory classes {@link TokenizerFactory},  * {@link TokenFilterFactory}, and {@link CharFilterFactory}.  *<p>You can create an instance of this Analyzer using the builder:  *<pre class="prettyprint">  * Analyzer ana = CustomAnalyzer.builder(Paths.get(&quot;/path/to/config/dir&quot;))  *   .withTokenizer(StandardTokenizerFactory.class)  *   .addTokenFilter(StandardFilterFactory.class)  *   .addTokenFilter(LowerCaseFilterFactory.class)  *   .addTokenFilter(StopFilterFactory.class,&quot;ignoreCase&quot;,&quot;false&quot;,&quot;words&quot;,&quot;stopwords.txt&quot;,&quot;format&quot;,&quot;wordset&quot;)  *   .build();  *</pre>  * The parameters passed to components are also used by Apache Solr and are documented  * on their corresponding factory classes. Refer to documentation of subclasses  * of {@link TokenizerFactory}, {@link TokenFilterFactory}, and {@link CharFilterFactory}.  *<p>You can also use the SPI names (as defined by {@link java.util.ServiceLoader} interface):  *<pre class="prettyprint">  * Analyzer ana = CustomAnalyzer.builder(Paths.get(&quot;/path/to/config/dir&quot;))  *   .withTokenizer(&quot;standard&quot;)  *   .addTokenFilter(&quot;standard&quot;)  *   .addTokenFilter(&quot;lowercase&quot;)  *   .addTokenFilter(&quot;stop&quot;,&quot;ignoreCase&quot;,&quot;false&quot;,&quot;words&quot;,&quot;stopwords.txt&quot;,&quot;format&quot;,&quot;wordset&quot;)  *   .build();  *</pre>  *<p>The list of names to be used for components can be looked up through:  * {@link TokenizerFactory#availableTokenizers()}, {@link TokenFilterFactory#availableTokenFilters()},  * and {@link CharFilterFactory#availableCharFilters()}.  */
end_comment

begin_class
DECL|class|CustomAnalyzer
specifier|public
specifier|final
class|class
name|CustomAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** Returns a builder for custom analyzers that loads all resources from classpath.    * All path names given must be absolute with package prefixes. */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
name|builder
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns a builder for custom analyzers that loads all resources from the given    * file system base directory. Place, e.g., stop word files there.    * Files that are not in the given directory are loaded from classpath. */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|Path
name|configDir
parameter_list|)
block|{
return|return
name|builder
argument_list|(
operator|new
name|FilesystemResourceLoader
argument_list|(
name|configDir
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a builder for custom analyzers that loads all resources using the given {@link ResourceLoader}. */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|loader
argument_list|)
return|;
block|}
DECL|field|charFilters
specifier|private
specifier|final
name|CharFilterFactory
index|[]
name|charFilters
decl_stmt|;
DECL|field|tokenizer
specifier|private
specifier|final
name|TokenizerFactory
name|tokenizer
decl_stmt|;
DECL|field|tokenFilters
specifier|private
specifier|final
name|TokenFilterFactory
index|[]
name|tokenFilters
decl_stmt|;
DECL|field|posIncGap
DECL|field|offsetGap
specifier|private
specifier|final
name|Integer
name|posIncGap
decl_stmt|,
name|offsetGap
decl_stmt|;
DECL|method|CustomAnalyzer
name|CustomAnalyzer
parameter_list|(
name|Version
name|defaultMatchVersion
parameter_list|,
name|CharFilterFactory
index|[]
name|charFilters
parameter_list|,
name|TokenizerFactory
name|tokenizer
parameter_list|,
name|TokenFilterFactory
index|[]
name|tokenFilters
parameter_list|,
name|Integer
name|posIncGap
parameter_list|,
name|Integer
name|offsetGap
parameter_list|)
block|{
name|this
operator|.
name|charFilters
operator|=
name|charFilters
expr_stmt|;
name|this
operator|.
name|tokenizer
operator|=
name|tokenizer
expr_stmt|;
name|this
operator|.
name|tokenFilters
operator|=
name|tokenFilters
expr_stmt|;
name|this
operator|.
name|posIncGap
operator|=
name|posIncGap
expr_stmt|;
name|this
operator|.
name|offsetGap
operator|=
name|offsetGap
expr_stmt|;
if|if
condition|(
name|defaultMatchVersion
operator|!=
literal|null
condition|)
block|{
name|setVersion
argument_list|(
name|defaultMatchVersion
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
for|for
control|(
specifier|final
name|CharFilterFactory
name|charFilter
range|:
name|charFilters
control|)
block|{
name|reader
operator|=
name|charFilter
operator|.
name|create
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|tk
init|=
name|tokenizer
operator|.
name|create
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|tk
decl_stmt|;
for|for
control|(
specifier|final
name|TokenFilterFactory
name|filter
range|:
name|tokenFilters
control|)
block|{
name|ts
operator|=
name|filter
operator|.
name|create
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tk
argument_list|,
name|ts
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// use default from Analyzer base class if null
return|return
operator|(
name|posIncGap
operator|==
literal|null
operator|)
condition|?
name|super
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
else|:
name|posIncGap
operator|.
name|intValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// use default from Analyzer base class if null
return|return
operator|(
name|offsetGap
operator|==
literal|null
operator|)
condition|?
name|super
operator|.
name|getOffsetGap
argument_list|(
name|fieldName
argument_list|)
else|:
name|offsetGap
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/** Returns the list of char filters that are used in this analyzer. */
DECL|method|getCharFilterFactories
specifier|public
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|getCharFilterFactories
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|charFilters
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the tokenizer that is used in this analyzer. */
DECL|method|getTokenizerFactory
specifier|public
name|TokenizerFactory
name|getTokenizerFactory
parameter_list|()
block|{
return|return
name|tokenizer
return|;
block|}
comment|/** Returns the list of token filters that are used in this analyzer. */
DECL|method|getTokenFilterFactories
specifier|public
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|getTokenFilterFactories
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|tokenFilters
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|CharFilterFactory
name|filter
range|:
name|charFilters
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filter
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|TokenFilterFactory
name|filter
range|:
name|tokenFilters
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Builder for {@link CustomAnalyzer}.    * @see CustomAnalyzer#builder()    * @see CustomAnalyzer#builder(Path)    * @see CustomAnalyzer#builder(ResourceLoader)    */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|loader
specifier|private
specifier|final
name|ResourceLoader
name|loader
decl_stmt|;
DECL|field|defaultMatchVersion
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|Version
argument_list|>
name|defaultMatchVersion
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|charFilters
specifier|private
specifier|final
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|tokenizer
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|TokenizerFactory
argument_list|>
name|tokenizer
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|tokenFilters
specifier|private
specifier|final
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|posIncGap
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|posIncGap
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|offsetGap
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|offsetGap
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|componentsAdded
specifier|private
name|boolean
name|componentsAdded
init|=
literal|false
decl_stmt|;
DECL|method|Builder
name|Builder
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/** This match version is passed as default to all tokenizers or filters. It is used unless you      * pass the parameter {code luceneMatchVersion} explicitly. It defaults to undefined, so the      * underlying factory will (in most cases) use {@link Version#LATEST}. */
DECL|method|withDefaultMatchVersion
specifier|public
name|Builder
name|withDefaultMatchVersion
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|version
argument_list|,
literal|"version may not be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|componentsAdded
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You may only set the default match version before adding tokenizers, "
operator|+
literal|"token filters, or char filters."
argument_list|)
throw|;
block|}
name|this
operator|.
name|defaultMatchVersion
operator|.
name|set
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Sets the position increment gap of the analyzer.      * The default is defined in the analyzer base class.      * @see Analyzer#getPositionIncrementGap(String)      */
DECL|method|withPositionIncrementGap
specifier|public
name|Builder
name|withPositionIncrementGap
parameter_list|(
name|int
name|posIncGap
parameter_list|)
block|{
if|if
condition|(
name|posIncGap
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"posIncGap must be>= 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|posIncGap
operator|.
name|set
argument_list|(
name|posIncGap
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Sets the offset gap of the analyzer. The default is defined      * in the analyzer base class.      * @see Analyzer#getOffsetGap(String)      */
DECL|method|withOffsetGap
specifier|public
name|Builder
name|withOffsetGap
parameter_list|(
name|int
name|offsetGap
parameter_list|)
block|{
if|if
condition|(
name|offsetGap
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offsetGap must be>= 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|offsetGap
operator|.
name|set
argument_list|(
name|offsetGap
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Uses the given tokenizer.      * @param factory class that is used to create the tokenizer.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|withTokenizer
specifier|public
name|Builder
name|withTokenizer
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|factory
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|withTokenizer
argument_list|(
name|factory
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Uses the given tokenizer.      * @param factory class that is used to create the tokenizer.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|withTokenizer
specifier|public
name|Builder
name|withTokenizer
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|factory
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|factory
argument_list|,
literal|"Tokenizer factory may not be null"
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|set
argument_list|(
name|applyResourceLoader
argument_list|(
name|newFactoryClassInstance
argument_list|(
name|factory
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Uses the given tokenizer.      * @param name is used to look up the factory with {@link TokenizerFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link TokenizerFactory#availableTokenizers()}.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|withTokenizer
specifier|public
name|Builder
name|withTokenizer
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|withTokenizer
argument_list|(
name|name
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Uses the given tokenizer.      * @param name is used to look up the factory with {@link TokenizerFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link TokenizerFactory#availableTokenizers()}.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|withTokenizer
specifier|public
name|Builder
name|withTokenizer
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|,
literal|"Tokenizer name may not be null"
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|set
argument_list|(
name|applyResourceLoader
argument_list|(
name|TokenizerFactory
operator|.
name|forName
argument_list|(
name|name
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds the given token filter.      * @param factory class that is used to create the token filter.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|addTokenFilter
specifier|public
name|Builder
name|addTokenFilter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilterFactory
argument_list|>
name|factory
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addTokenFilter
argument_list|(
name|factory
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Adds the given token filter.      * @param factory class that is used to create the token filter.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|addTokenFilter
specifier|public
name|Builder
name|addTokenFilter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilterFactory
argument_list|>
name|factory
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|factory
argument_list|,
literal|"TokenFilter name may not be null"
argument_list|)
expr_stmt|;
name|tokenFilters
operator|.
name|add
argument_list|(
name|applyResourceLoader
argument_list|(
name|newFactoryClassInstance
argument_list|(
name|factory
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds the given token filter.      * @param name is used to look up the factory with {@link TokenFilterFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link TokenFilterFactory#availableTokenFilters()}.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|addTokenFilter
specifier|public
name|Builder
name|addTokenFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addTokenFilter
argument_list|(
name|name
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Adds the given token filter.      * @param name is used to look up the factory with {@link TokenFilterFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link TokenFilterFactory#availableTokenFilters()}.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|addTokenFilter
specifier|public
name|Builder
name|addTokenFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|,
literal|"TokenFilter name may not be null"
argument_list|)
expr_stmt|;
name|tokenFilters
operator|.
name|add
argument_list|(
name|applyResourceLoader
argument_list|(
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
name|name
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds the given char filter.      * @param factory class that is used to create the char filter.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|addCharFilter
specifier|public
name|Builder
name|addCharFilter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CharFilterFactory
argument_list|>
name|factory
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addCharFilter
argument_list|(
name|factory
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Adds the given char filter.      * @param factory class that is used to create the char filter.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|addCharFilter
specifier|public
name|Builder
name|addCharFilter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|CharFilterFactory
argument_list|>
name|factory
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|factory
argument_list|,
literal|"CharFilter name may not be null"
argument_list|)
expr_stmt|;
name|charFilters
operator|.
name|add
argument_list|(
name|applyResourceLoader
argument_list|(
name|newFactoryClassInstance
argument_list|(
name|factory
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds the given char filter.      * @param name is used to look up the factory with {@link CharFilterFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link CharFilterFactory#availableCharFilters()}.      * @param params a list of factory string params as key/value pairs.      *  The number of parameters must be an even number, as they are pairs.      */
DECL|method|addCharFilter
specifier|public
name|Builder
name|addCharFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|addCharFilter
argument_list|(
name|name
argument_list|,
name|paramsToMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
comment|/** Adds the given char filter.      * @param name is used to look up the factory with {@link CharFilterFactory#forName(String, Map)}.      *  The list of possible names can be looked up with {@link CharFilterFactory#availableCharFilters()}.      * @param params the map of parameters to be passed to factory. The map must be modifiable.      */
DECL|method|addCharFilter
specifier|public
name|Builder
name|addCharFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|,
literal|"CharFilter name may not be null"
argument_list|)
expr_stmt|;
name|charFilters
operator|.
name|add
argument_list|(
name|applyResourceLoader
argument_list|(
name|CharFilterFactory
operator|.
name|forName
argument_list|(
name|name
argument_list|,
name|applyDefaultParams
argument_list|(
name|params
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|componentsAdded
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Builds the analyzer. */
DECL|method|build
specifier|public
name|CustomAnalyzer
name|build
parameter_list|()
block|{
if|if
condition|(
name|tokenizer
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You have to set at least a tokenizer."
argument_list|)
throw|;
block|}
return|return
operator|new
name|CustomAnalyzer
argument_list|(
name|defaultMatchVersion
operator|.
name|get
argument_list|()
argument_list|,
name|charFilters
operator|.
name|toArray
argument_list|(
operator|new
name|CharFilterFactory
index|[
name|charFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|tokenizer
operator|.
name|get
argument_list|()
argument_list|,
name|tokenFilters
operator|.
name|toArray
argument_list|(
operator|new
name|TokenFilterFactory
index|[
name|tokenFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|posIncGap
operator|.
name|get
argument_list|()
argument_list|,
name|offsetGap
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|applyDefaultParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|applyDefaultParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|defaultMatchVersion
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|AbstractAnalysisFactory
operator|.
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|AbstractAnalysisFactory
operator|.
name|LUCENE_MATCH_VERSION_PARAM
argument_list|,
name|defaultMatchVersion
operator|.
name|get
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|paramsToMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramsToMap
parameter_list|(
name|String
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|.
name|length
operator|%
literal|2
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Key-value pairs expected, so the number of params must be even."
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
literal|"Key of param may not be null."
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|applyResourceLoader
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|applyResourceLoader
parameter_list|(
name|T
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|factory
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|factory
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
block|}
block|}
end_class

end_unit

