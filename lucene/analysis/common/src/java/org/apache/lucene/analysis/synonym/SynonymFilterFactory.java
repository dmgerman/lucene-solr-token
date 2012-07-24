begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|text
operator|.
name|ParseException
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
name|core
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|synonym
operator|.
name|SynonymFilter
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
name|synonym
operator|.
name|SynonymMap
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
name|synonym
operator|.
name|SolrSynonymParser
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
name|synonym
operator|.
name|WordnetSynonymParser
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
name|*
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
comment|/**  * Factory for {@link SynonymFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_synonym" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt"   *             format="solr" ignoreCase="false" expand="true"   *             tokenizerFactory="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment

begin_class
DECL|class|SynonymFilterFactory
specifier|public
class|class
name|SynonymFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|map
specifier|private
name|SynonymMap
name|map
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
comment|// if the fst is null, it means there's actually no synonyms... just return the original stream
comment|// as there is nothing to do here.
return|return
name|map
operator|.
name|fst
operator|==
literal|null
condition|?
name|input
else|:
operator|new
name|SynonymFilter
argument_list|(
name|input
argument_list|,
name|map
argument_list|,
name|ignoreCase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
specifier|final
name|boolean
name|ignoreCase
init|=
name|getBoolean
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|String
name|tf
init|=
name|args
operator|.
name|get
argument_list|(
literal|"tokenizerFactory"
argument_list|)
decl_stmt|;
specifier|final
name|TokenizerFactory
name|factory
init|=
name|tf
operator|==
literal|null
condition|?
literal|null
else|:
name|loadTokenizerFactory
argument_list|(
name|loader
argument_list|,
name|tf
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|==
literal|null
condition|?
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|reader
argument_list|)
else|:
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|ignoreCase
condition|?
operator|new
name|LowerCaseFilter
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
name|tokenizer
argument_list|)
else|:
name|tokenizer
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|String
name|format
init|=
name|args
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|format
operator|==
literal|null
operator|||
name|format
operator|.
name|equals
argument_list|(
literal|"solr"
argument_list|)
condition|)
block|{
comment|// TODO: expose dedup as a parameter?
name|map
operator|=
name|loadSolrSynonyms
argument_list|(
name|loader
argument_list|,
literal|true
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|format
operator|.
name|equals
argument_list|(
literal|"wordnet"
argument_list|)
condition|)
block|{
name|map
operator|=
name|loadWordnetSynonyms
argument_list|(
name|loader
argument_list|,
literal|true
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: somehow make this more pluggable
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Unrecognized synonyms format: "
operator|+
name|format
argument_list|)
throw|;
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
name|InitializationException
argument_list|(
literal|"Exception thrown while loading synonyms"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load synonyms from the solr format, "format=solr".    */
DECL|method|loadSolrSynonyms
specifier|private
name|SynonymMap
name|loadSolrSynonyms
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|boolean
name|dedup
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
specifier|final
name|boolean
name|expand
init|=
name|getBoolean
argument_list|(
literal|"expand"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|synonyms
init|=
name|args
operator|.
name|get
argument_list|(
literal|"synonyms"
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonyms
operator|==
literal|null
condition|)
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Missing required argument 'synonyms'."
argument_list|)
throw|;
name|CharsetDecoder
name|decoder
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
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
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
name|dedup
argument_list|,
name|expand
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|File
name|synonymFile
init|=
operator|new
name|File
argument_list|(
name|synonyms
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonymFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|decoder
operator|.
name|reset
argument_list|()
expr_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|synonyms
argument_list|)
argument_list|,
name|decoder
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|synonyms
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|decoder
operator|.
name|reset
argument_list|()
expr_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
argument_list|,
name|decoder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parser
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Load synonyms from the wordnet format, "format=wordnet".    */
DECL|method|loadWordnetSynonyms
specifier|private
name|SynonymMap
name|loadWordnetSynonyms
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|boolean
name|dedup
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
specifier|final
name|boolean
name|expand
init|=
name|getBoolean
argument_list|(
literal|"expand"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|synonyms
init|=
name|args
operator|.
name|get
argument_list|(
literal|"synonyms"
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonyms
operator|==
literal|null
condition|)
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Missing required argument 'synonyms'."
argument_list|)
throw|;
name|CharsetDecoder
name|decoder
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
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
name|WordnetSynonymParser
name|parser
init|=
operator|new
name|WordnetSynonymParser
argument_list|(
name|dedup
argument_list|,
name|expand
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|File
name|synonymFile
init|=
operator|new
name|File
argument_list|(
name|synonyms
argument_list|)
decl_stmt|;
if|if
condition|(
name|synonymFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|decoder
operator|.
name|reset
argument_list|()
expr_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|synonyms
argument_list|)
argument_list|,
name|decoder
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|synonyms
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|decoder
operator|.
name|reset
argument_list|()
expr_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
argument_list|,
name|decoder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parser
operator|.
name|build
argument_list|()
return|;
block|}
comment|// (there are no tests for this functionality)
DECL|method|loadTokenizerFactory
specifier|private
name|TokenizerFactory
name|loadTokenizerFactory
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|cname
parameter_list|)
block|{
name|TokenizerFactory
name|tokFactory
init|=
name|loader
operator|.
name|newInstance
argument_list|(
name|cname
argument_list|,
name|TokenizerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokFactory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|luceneMatchVersion
argument_list|)
expr_stmt|;
name|tokFactory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokFactory
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|tokFactory
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|tokFactory
return|;
block|}
block|}
end_class

end_unit

