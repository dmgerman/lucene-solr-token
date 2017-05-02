begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|BM25Similarity
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
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|similarities
operator|.
name|Similarity
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|params
operator|.
name|SolrParams
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
name|SimilarityFactory
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
name|util
operator|.
name|PayloadDecoder
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
name|util
operator|.
name|PayloadUtils
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import

begin_comment
comment|/**  *<p>  *<code>SimilarityFactory</code> that returns a global {@link PerFieldSimilarityWrapper}  * that delegates to the field type, if it's configured.  For field types that  * do not have a<code>Similarity</code> explicitly configured, the global<code>Similarity</code>   * will use per fieldtype defaults -- either based on an explicitly configured   *<code>defaultSimFromFieldType</code> a sensible default depending on the {@link Version}   * matching configured:  *</p>  *<ul>  *<li><code>luceneMatchVersion&lt; 6.0</code> = {@link ClassicSimilarity}</li>  *<li><code>luceneMatchVersion&gt;= 6.0</code> = {@link BM25Similarity}</li>  *</ul>  *<p>  * The<code>defaultSimFromFieldType</code> option accepts the name of any fieldtype, and uses   * whatever<code>Similarity</code> is explicitly configured for that fieldType as the default for  * all other field types.  For example:  *</p>  *<pre class="prettyprint">  *&lt;similarity class="solr.SchemaSimilarityFactory"&gt;  *&lt;str name="defaultSimFromFieldType"&gt;type-using-custom-dfr&lt;/str&gt;  *&lt;/similarity&gt;  *   ...  *&lt;fieldType name="type-using-custom-dfr" class="solr.TextField"&gt;  *     ...  *&lt;similarity class="solr.DFRSimilarityFactory"&gt;  *&lt;str name="basicModel"&gt;I(F)&lt;/str&gt;  *&lt;str name="afterEffect"&gt;B&lt;/str&gt;  *&lt;str name="normalization"&gt;H3&lt;/str&gt;  *&lt;float name="mu"&gt;900&lt;/float&gt;  *&lt;/similarity&gt;  *&lt;/fieldType&gt;  *</pre>  *<p>  * In the example above, any fieldtypes that do not define their own<code>&lt;/similarity/&gt;</code>   * will use the<code>Similarity</code> configured for the<code>type-using-custom-dfr</code>.  *</p>  *   *<p>  *<b>NOTE:</b> Users should be aware that even when this factory uses a single default   *<code>Similarity</code> for some or all fields in a Query, the behavior can be inconsistent   * with the behavior of explicitly configuring that same<code>Similarity</code> globally, because   * of differences in how some multi-field / multi-clause behavior is defined in   *<code>PerFieldSimilarityWrapper</code>.  *</p>  *  * @see FieldType#getSimilarity  */
end_comment

begin_class
DECL|class|SchemaSimilarityFactory
specifier|public
class|class
name|SchemaSimilarityFactory
extends|extends
name|SimilarityFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|INIT_OPT
specifier|private
specifier|static
specifier|final
name|String
name|INIT_OPT
init|=
literal|"defaultSimFromFieldType"
decl_stmt|;
DECL|field|defaultSimFromFieldType
specifier|private
name|String
name|defaultSimFromFieldType
decl_stmt|;
comment|// set by init, if null use sensible implicit default
DECL|field|core
specifier|private
specifier|volatile
name|SolrCore
name|core
decl_stmt|;
comment|// set by inform(SolrCore)
DECL|field|similarity
specifier|private
specifier|volatile
name|Similarity
name|similarity
decl_stmt|;
comment|// lazy instantiated
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|args
parameter_list|)
block|{
name|defaultSimFromFieldType
operator|=
name|args
operator|.
name|get
argument_list|(
name|INIT_OPT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|core
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"SchemaSimilarityFactory can not be used until SolrCoreAware.inform has been called"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|similarity
condition|)
block|{
comment|// Need to instantiate lazily, can't do this in inform(SolrCore) because of chicken/egg
comment|// circular initialization hell with core.getLatestSchema() to lookup defaultSimFromFieldType
name|Similarity
name|defaultSim
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|defaultSimFromFieldType
condition|)
block|{
comment|// nothing configured, choose a sensible implicit default...
name|defaultSim
operator|=
name|this
operator|.
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|)
condition|?
operator|new
name|BM25Similarity
argument_list|()
else|:
operator|new
name|ClassicSimilarity
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|FieldType
name|defSimFT
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeByName
argument_list|(
name|defaultSimFromFieldType
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|defSimFT
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"SchemaSimilarityFactory configured with "
operator|+
name|INIT_OPT
operator|+
literal|"='"
operator|+
name|defaultSimFromFieldType
operator|+
literal|"' but that<fieldType> does not exist"
argument_list|)
throw|;
block|}
name|defaultSim
operator|=
name|defSimFT
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|defaultSim
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"SchemaSimilarityFactory configured with "
operator|+
name|INIT_OPT
operator|+
literal|"='"
operator|+
name|defaultSimFromFieldType
operator|+
literal|"' but that<fieldType> does not define a<similarity>"
argument_list|)
throw|;
block|}
block|}
name|similarity
operator|=
operator|new
name|SchemaSimilarity
argument_list|(
name|defaultSim
argument_list|)
expr_stmt|;
block|}
return|return
name|similarity
return|;
block|}
DECL|class|SchemaSimilarity
specifier|private
class|class
name|SchemaSimilarity
extends|extends
name|PerFieldSimilarityWrapper
block|{
DECL|field|defaultSimilarity
specifier|private
name|Similarity
name|defaultSimilarity
decl_stmt|;
DECL|field|decoders
specifier|private
name|HashMap
argument_list|<
name|FieldType
argument_list|,
name|PayloadDecoder
argument_list|>
name|decoders
decl_stmt|;
comment|// cache to avoid scanning token filters repeatedly, unnecessarily
DECL|method|SchemaSimilarity
specifier|public
name|SchemaSimilarity
parameter_list|(
name|Similarity
name|defaultSimilarity
parameter_list|)
block|{
name|this
operator|.
name|defaultSimilarity
operator|=
name|defaultSimilarity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldType
name|fieldType
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
return|return
name|defaultSimilarity
return|;
block|}
else|else
block|{
name|Similarity
name|similarity
init|=
name|fieldType
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
name|similarity
operator|=
name|similarity
operator|==
literal|null
condition|?
name|defaultSimilarity
else|:
name|similarity
expr_stmt|;
comment|// Payload score handling: if field type has index-time payload encoding, wrap and computePayloadFactor accordingly
if|if
condition|(
name|decoders
operator|==
literal|null
condition|)
name|decoders
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|PayloadDecoder
name|decoder
decl_stmt|;
if|if
condition|(
operator|!
name|decoders
operator|.
name|containsKey
argument_list|(
name|fieldType
argument_list|)
condition|)
block|{
name|decoders
operator|.
name|put
argument_list|(
name|fieldType
argument_list|,
name|PayloadUtils
operator|.
name|getPayloadDecoder
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|decoder
operator|=
name|decoders
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
if|if
condition|(
name|decoder
operator|!=
literal|null
condition|)
name|similarity
operator|=
operator|new
name|PayloadScoringSimilarityWrapper
argument_list|(
name|similarity
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
return|return
name|similarity
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SchemaSimilarity. Default: "
operator|+
operator|(
operator|(
name|get
argument_list|(
literal|""
argument_list|)
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|get
argument_list|(
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

