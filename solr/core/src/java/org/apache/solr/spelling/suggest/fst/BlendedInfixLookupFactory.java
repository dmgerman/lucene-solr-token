begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|fst
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
name|IOException
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
name|Locale
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
name|search
operator|.
name|suggest
operator|.
name|Lookup
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|BlendedInfixSuggester
operator|.
name|BlenderType
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|BlendedInfixSuggester
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
name|store
operator|.
name|FSDirectory
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import

begin_comment
comment|/**  * Factory for {@link BlendedInfixLookupFactory}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BlendedInfixLookupFactory
specifier|public
class|class
name|BlendedInfixLookupFactory
extends|extends
name|AnalyzingInfixLookupFactory
block|{
comment|/**    * Blender type used to calculate weight coefficient using the position    * of the first matching word    *</p>    * Available blender types are:</br>    *  linear: weight*(1 - 0.10*position) [default]</br>    *  reciprocal: weight/(1+position)     */
DECL|field|BLENDER_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|BLENDER_TYPE
init|=
literal|"blenderType"
decl_stmt|;
DECL|field|EXPONENT
specifier|private
specifier|static
specifier|final
name|String
name|EXPONENT
init|=
literal|"exponent"
decl_stmt|;
comment|/**     * Factor to multiply the number of searched elements    * Default is 10    */
DECL|field|NUM_FACTOR
specifier|private
specifier|static
specifier|final
name|String
name|NUM_FACTOR
init|=
literal|"numFactor"
decl_stmt|;
comment|/**     * Default path where the index for the suggester is stored/loaded from    * */
DECL|field|DEFAULT_INDEX_PATH
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_INDEX_PATH
init|=
literal|"blendedInfixSuggesterIndexDir"
decl_stmt|;
comment|/**    * File name for the automaton.    */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"bifsta.bin"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
comment|// mandatory parameter
name|Object
name|fieldTypeName
init|=
name|params
operator|.
name|get
argument_list|(
name|QUERY_ANALYZER
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldTypeName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|QUERY_ANALYZER
operator|+
literal|" parameter is mandatory"
argument_list|)
throw|;
block|}
name|FieldType
name|ft
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeByName
argument_list|(
name|fieldTypeName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|fieldTypeName
operator|.
name|toString
argument_list|()
operator|+
literal|" is not defined in the schema"
argument_list|)
throw|;
block|}
name|Analyzer
name|indexAnalyzer
init|=
name|ft
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|Analyzer
name|queryAnalyzer
init|=
name|ft
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
comment|// optional parameters
name|String
name|indexPath
init|=
name|params
operator|.
name|get
argument_list|(
name|INDEX_PATH
argument_list|)
operator|!=
literal|null
condition|?
name|params
operator|.
name|get
argument_list|(
name|INDEX_PATH
argument_list|)
operator|.
name|toString
argument_list|()
else|:
name|DEFAULT_INDEX_PATH
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
operator|.
name|isAbsolute
argument_list|()
operator|==
literal|false
condition|)
block|{
name|indexPath
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|indexPath
expr_stmt|;
block|}
name|int
name|minPrefixChars
init|=
name|params
operator|.
name|get
argument_list|(
name|MIN_PREFIX_CHARS
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MIN_PREFIX_CHARS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
decl_stmt|;
name|boolean
name|allTermsRequired
init|=
name|params
operator|.
name|get
argument_list|(
name|ALL_TERMS_REQUIRED
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|ALL_TERMS_REQUIRED
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_ALL_TERMS_REQUIRED
decl_stmt|;
name|boolean
name|highlight
init|=
name|params
operator|.
name|get
argument_list|(
name|HIGHLIGHT
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|HIGHLIGHT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_HIGHLIGHT
decl_stmt|;
name|BlenderType
name|blenderType
init|=
name|getBlenderType
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|BLENDER_TYPE
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numFactor
init|=
name|params
operator|.
name|get
argument_list|(
name|NUM_FACTOR
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|NUM_FACTOR
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|BlendedInfixSuggester
operator|.
name|DEFAULT_NUM_FACTOR
decl_stmt|;
name|Double
name|exponent
init|=
name|params
operator|.
name|get
argument_list|(
name|EXPONENT
argument_list|)
operator|==
literal|null
condition|?
literal|null
else|:
name|Double
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|EXPONENT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|BlendedInfixSuggester
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|indexPath
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
argument_list|,
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|minPrefixChars
argument_list|,
name|blenderType
argument_list|,
name|numFactor
argument_list|,
name|exponent
argument_list|,
literal|true
argument_list|,
name|allTermsRequired
argument_list|,
name|highlight
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|,
name|int
name|num
parameter_list|,
name|boolean
name|allTermsRequired
parameter_list|,
name|boolean
name|doHighlight
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
name|super
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|contexts
argument_list|,
name|num
argument_list|,
name|allTermsRequired
argument_list|,
name|doHighlight
argument_list|)
decl_stmt|;
if|if
condition|(
name|doHighlight
condition|)
block|{
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|LookupResult
name|hit
range|:
name|res
control|)
block|{
name|res2
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|hit
operator|.
name|highlightKey
operator|.
name|toString
argument_list|()
argument_list|,
name|hit
operator|.
name|highlightKey
argument_list|,
name|hit
operator|.
name|value
argument_list|,
name|hit
operator|.
name|payload
argument_list|,
name|hit
operator|.
name|contexts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|res
operator|=
name|res2
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
return|;
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
annotation|@
name|Override
DECL|method|storeFileName
specifier|public
name|String
name|storeFileName
parameter_list|()
block|{
return|return
name|FILENAME
return|;
block|}
DECL|method|getBlenderType
specifier|private
name|BlenderType
name|getBlenderType
parameter_list|(
name|Object
name|blenderTypeParam
parameter_list|)
block|{
name|BlenderType
name|blenderType
init|=
name|BlenderType
operator|.
name|POSITION_LINEAR
decl_stmt|;
if|if
condition|(
name|blenderTypeParam
operator|!=
literal|null
condition|)
block|{
name|String
name|blenderTypeStr
init|=
name|blenderTypeParam
operator|.
name|toString
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|blenderType
operator|=
name|BlenderType
operator|.
name|valueOf
argument_list|(
name|blenderTypeStr
argument_list|)
expr_stmt|;
block|}
return|return
name|blenderType
return|;
block|}
block|}
end_class

end_unit

