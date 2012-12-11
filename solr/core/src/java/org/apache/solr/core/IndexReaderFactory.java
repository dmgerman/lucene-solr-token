begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|IOException
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexReader
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
name|Directory
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_comment
comment|/**  * Factory used to build a new IndexReader instance.  */
end_comment

begin_class
DECL|class|IndexReaderFactory
specifier|public
specifier|abstract
class|class
name|IndexReaderFactory
implements|implements
name|NamedListInitializedPlugin
block|{
DECL|field|termInfosIndexDivisor
specifier|protected
name|int
name|termInfosIndexDivisor
init|=
literal|1
decl_stmt|;
comment|//IndexReader.DEFAULT_TERMS_INDEX_DIVISOR;  Set this once Lucene makes this public.
comment|/**    * Potentially initializes {@link #termInfosIndexDivisor}.  Overriding classes should call super.init() in order    * to make sure termInfosIndexDivisor is set.    *<p>    *<code>init</code> will be called just once, immediately after creation.    *<p>    * The args are user-level initialization parameters that may be specified    * when declaring an indexReaderFactory in solrconfig.xml    *    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"setTermIndexDivisor"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|termInfosIndexDivisor
operator|=
name|v
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    *    * @return The setting of {@link #termInfosIndexDivisor}     */
DECL|method|getTermInfosIndexDivisor
specifier|public
name|int
name|getTermInfosIndexDivisor
parameter_list|()
block|{
return|return
name|termInfosIndexDivisor
return|;
block|}
comment|/**    * Creates a new IndexReader instance using the given Directory.    *     * @param indexDir indexDir index location    * @param core {@link SolrCore} instance where this reader will be used. NOTE:    * this SolrCore instance may not be fully configured yet, but basic things like    * {@link SolrCore#getCoreDescriptor()}, {@link SolrCore#getSchema()} and    * {@link SolrCore#getSolrConfig()} are valid.    * @return An IndexReader instance    * @throws IOException If there is a low-level I/O error.    */
DECL|method|newReader
specifier|public
specifier|abstract
name|DirectoryReader
name|newReader
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

