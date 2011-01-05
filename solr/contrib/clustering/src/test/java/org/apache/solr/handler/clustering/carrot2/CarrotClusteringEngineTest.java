begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Term
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
name|MatchAllDocsQuery
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
name|Query
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
name|Sort
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
name|TermQuery
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
name|SolrDocument
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
name|SolrDocumentList
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
name|ModifiableSolrParams
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
name|handler
operator|.
name|clustering
operator|.
name|AbstractClusteringTestCase
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
name|handler
operator|.
name|clustering
operator|.
name|ClusteringComponent
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|SolrIndexSearcher
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
name|RefCounted
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
name|SolrPluginUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|attribute
operator|.
name|AttributeUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|CarrotClusteringEngineTest
specifier|public
class|class
name|CarrotClusteringEngineTest
extends|extends
name|AbstractClusteringTestCase
block|{
annotation|@
name|Test
DECL|method|testCarrotLingo
specifier|public
name|void
name|testCarrotLingo
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note: the expected number of clusters may change after upgrading Carrot2
comment|// due to e.g. internal improvements or tuning of Carrot2 clustering.
specifier|final
name|int
name|expectedNumClusters
init|=
literal|10
decl_stmt|;
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"default"
argument_list|)
argument_list|,
name|expectedNumClusters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProduceSummary
specifier|public
name|void
name|testProduceSummary
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
name|CarrotParams
operator|.
name|SNIPPET_FIELD_NAME
argument_list|,
literal|"snippet"
argument_list|)
expr_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
name|CarrotParams
operator|.
name|SUMMARY_FRAGSIZE
argument_list|,
literal|"200"
argument_list|)
expr_stmt|;
comment|//how do we validate this?
comment|// Note: the expected number of clusters may change after upgrading Carrot2
comment|// due to e.g. internal improvements or tuning of Carrot2 clustering.
specifier|final
name|int
name|expectedNumClusters
init|=
literal|15
decl_stmt|;
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"default"
argument_list|)
argument_list|,
name|numberOfDocs
operator|-
literal|2
comment|/*two don't have mining in the snippet*/
argument_list|,
name|expectedNumClusters
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"snippet"
argument_list|,
literal|"mine"
argument_list|)
argument_list|)
argument_list|,
name|solrParams
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCarrotStc
specifier|public
name|void
name|testCarrotStc
parameter_list|()
throws|throws
name|Exception
block|{
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"stc"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithoutSubclusters
specifier|public
name|void
name|testWithoutSubclusters
parameter_list|()
throws|throws
name|Exception
block|{
name|checkClusters
argument_list|(
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"mock"
argument_list|)
argument_list|,
name|this
operator|.
name|numberOfDocs
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithSubclusters
specifier|public
name|void
name|testWithSubclusters
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CarrotParams
operator|.
name|OUTPUT_SUB_CLUSTERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkClusters
argument_list|(
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"mock"
argument_list|)
argument_list|,
name|this
operator|.
name|numberOfDocs
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumDescriptions
specifier|public
name|void
name|testNumDescriptions
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|AttributeUtils
operator|.
name|getKey
argument_list|(
name|MockClusteringAlgorithm
operator|.
name|class
argument_list|,
literal|"labels"
argument_list|)
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CarrotParams
operator|.
name|NUM_DESCRIPTIONS
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|checkClusters
argument_list|(
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"mock"
argument_list|)
argument_list|,
name|this
operator|.
name|numberOfDocs
argument_list|,
name|params
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCarrotAttributePassing
specifier|public
name|void
name|testCarrotAttributePassing
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|AttributeUtils
operator|.
name|getKey
argument_list|(
name|MockClusteringAlgorithm
operator|.
name|class
argument_list|,
literal|"depth"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|AttributeUtils
operator|.
name|getKey
argument_list|(
name|MockClusteringAlgorithm
operator|.
name|class
argument_list|,
literal|"labels"
argument_list|)
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|checkClusters
argument_list|(
name|checkEngine
argument_list|(
name|getClusteringEngine
argument_list|(
literal|"mock"
argument_list|)
argument_list|,
name|this
operator|.
name|numberOfDocs
argument_list|,
name|params
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getClusteringEngine
specifier|private
name|CarrotClusteringEngine
name|getClusteringEngine
parameter_list|(
name|String
name|engineName
parameter_list|)
block|{
name|ClusteringComponent
name|comp
init|=
operator|(
name|ClusteringComponent
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearchComponent
argument_list|(
literal|"clustering"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"clustering component should not be null"
argument_list|,
name|comp
argument_list|)
expr_stmt|;
name|CarrotClusteringEngine
name|engine
init|=
operator|(
name|CarrotClusteringEngine
operator|)
name|comp
operator|.
name|getSearchClusteringEngines
argument_list|()
operator|.
name|get
argument_list|(
name|engineName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"clustering engine for name: "
operator|+
name|engineName
operator|+
literal|" should not be null"
argument_list|,
name|engine
argument_list|)
expr_stmt|;
return|return
name|engine
return|;
block|}
DECL|method|checkEngine
specifier|private
name|List
name|checkEngine
parameter_list|(
name|CarrotClusteringEngine
name|engine
parameter_list|,
name|int
name|expectedNumClusters
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|checkEngine
argument_list|(
name|engine
argument_list|,
name|numberOfDocs
argument_list|,
name|expectedNumClusters
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
return|;
block|}
DECL|method|checkEngine
specifier|private
name|List
name|checkEngine
parameter_list|(
name|CarrotClusteringEngine
name|engine
parameter_list|,
name|int
name|expectedNumClusters
parameter_list|,
name|SolrParams
name|clusteringParams
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|checkEngine
argument_list|(
name|engine
argument_list|,
name|numberOfDocs
argument_list|,
name|expectedNumClusters
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|clusteringParams
argument_list|)
return|;
block|}
DECL|method|checkEngine
specifier|private
name|List
name|checkEngine
parameter_list|(
name|CarrotClusteringEngine
name|engine
parameter_list|,
name|int
name|expectedNumDocs
parameter_list|,
name|int
name|expectedNumClusters
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrParams
name|clusteringParams
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get all documents to cluster
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|ref
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|DocList
name|docList
decl_stmt|;
try|try
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
name|docList
operator|=
name|searcher
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
operator|(
name|Query
operator|)
literal|null
argument_list|,
operator|new
name|Sort
argument_list|()
argument_list|,
literal|0
argument_list|,
name|numberOfDocs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"docList size"
argument_list|,
name|expectedNumDocs
argument_list|,
name|docList
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
name|CarrotParams
operator|.
name|PRODUCE_SUMMARY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
name|clusteringParams
argument_list|)
expr_stmt|;
comment|// Perform clustering
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|solrParams
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
init|=
operator|new
name|HashMap
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
argument_list|(
name|docList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|solrDocList
init|=
name|SolrPluginUtils
operator|.
name|docListToSolrDocumentList
argument_list|(
name|docList
argument_list|,
name|searcher
argument_list|,
name|engine
operator|.
name|getFieldsToLoad
argument_list|(
name|req
argument_list|)
argument_list|,
name|docIds
argument_list|)
decl_stmt|;
name|List
name|results
init|=
operator|(
name|List
operator|)
name|engine
operator|.
name|cluster
argument_list|(
name|query
argument_list|,
name|solrDocList
argument_list|,
name|docIds
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of clusters: "
operator|+
name|results
argument_list|,
name|expectedNumClusters
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkClusters
argument_list|(
name|results
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
finally|finally
block|{
name|ref
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkClusters
specifier|private
name|void
name|checkClusters
parameter_list|(
name|List
name|results
parameter_list|,
name|int
name|expectedDocCount
parameter_list|,
name|int
name|expectedLabelCount
parameter_list|,
name|int
name|expectedSubclusterCount
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
name|cluster
init|=
operator|(
name|NamedList
operator|)
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|checkCluster
argument_list|(
name|cluster
argument_list|,
name|expectedDocCount
argument_list|,
name|expectedLabelCount
argument_list|,
name|expectedSubclusterCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkClusters
specifier|private
name|void
name|checkClusters
parameter_list|(
name|List
name|results
parameter_list|,
name|boolean
name|hasSubclusters
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|checkCluster
argument_list|(
operator|(
name|NamedList
operator|)
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|hasSubclusters
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkCluster
specifier|private
name|void
name|checkCluster
parameter_list|(
name|NamedList
name|cluster
parameter_list|,
name|boolean
name|hasSubclusters
parameter_list|)
block|{
name|List
name|docs
init|=
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"docs is null and it shouldn't be"
argument_list|,
name|docs
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|id
init|=
operator|(
name|String
operator|)
name|docs
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"id is null and it shouldn't be"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|List
name|labels
init|=
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"labels"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"labels is null but it shouldn't be"
argument_list|,
name|labels
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasSubclusters
condition|)
block|{
name|List
name|subclusters
init|=
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"clusters"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"subclusters is null but it shouldn't be"
argument_list|,
name|subclusters
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkCluster
specifier|private
name|void
name|checkCluster
parameter_list|(
name|NamedList
name|cluster
parameter_list|,
name|int
name|expectedDocCount
parameter_list|,
name|int
name|expectedLabelCount
parameter_list|,
name|int
name|expectedSubclusterCount
parameter_list|)
block|{
name|checkCluster
argument_list|(
name|cluster
argument_list|,
name|expectedSubclusterCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of docs in cluster"
argument_list|,
name|expectedDocCount
argument_list|,
operator|(
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"docs"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of labels in cluster"
argument_list|,
name|expectedLabelCount
argument_list|,
operator|(
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"labels"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedSubclusterCount
operator|>
literal|0
condition|)
block|{
name|List
name|subclusters
init|=
operator|(
name|List
operator|)
name|cluster
operator|.
name|get
argument_list|(
literal|"clusters"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"numClusters"
argument_list|,
name|expectedSubclusterCount
argument_list|,
name|subclusters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of subclusters in cluster"
argument_list|,
name|expectedSubclusterCount
argument_list|,
name|subclusters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

