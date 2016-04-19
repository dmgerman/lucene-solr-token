begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|LuceneTestCase
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|ops
operator|.
name|GroupOperation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExplanation
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParser
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|CountMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MaxMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MeanMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|Metric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|MinMetric
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|metrics
operator|.
name|SumMetric
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

begin_comment
comment|/**  **/
end_comment

begin_class
DECL|class|StreamExpressionToExplanationTest
specifier|public
class|class
name|StreamExpressionToExplanationTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|factory
specifier|private
name|StreamFactory
name|factory
decl_stmt|;
DECL|method|StreamExpressionToExplanationTest
specifier|public
name|StreamExpressionToExplanationTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withCollectionZkHost
argument_list|(
literal|"collection1"
argument_list|,
literal|"testhost:1234"
argument_list|)
operator|.
name|withCollectionZkHost
argument_list|(
literal|"collection2"
argument_list|,
literal|"testhost:1234"
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"search"
argument_list|,
name|CloudSolrStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"select"
argument_list|,
name|SelectStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"merge"
argument_list|,
name|MergeStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"unique"
argument_list|,
name|UniqueStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"top"
argument_list|,
name|RankStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"reduce"
argument_list|,
name|ReducerStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"group"
argument_list|,
name|GroupOperation
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"update"
argument_list|,
name|UpdateStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"stats"
argument_list|,
name|StatsStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"facet"
argument_list|,
name|FacetStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"jdbc"
argument_list|,
name|JDBCStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"intersect"
argument_list|,
name|IntersectStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"complement"
argument_list|,
name|ComplementStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"count"
argument_list|,
name|CountMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"sum"
argument_list|,
name|SumMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"min"
argument_list|,
name|MinMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"max"
argument_list|,
name|MaxMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"avg"
argument_list|,
name|MeanMetric
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"daemon"
argument_list|,
name|DaemonStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"topic"
argument_list|,
name|TopicStream
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloudSolrStream
specifier|public
name|void
name|testCloudSolrStream
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|CloudSolrStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"search"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CloudSolrStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectStream
specifier|public
name|void
name|testSelectStream
parameter_list|()
throws|throws
name|Exception
block|{
name|SelectStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|SelectStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"select(\"a_s as fieldA\", search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\"))"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"select"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SelectStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDaemonStream
specifier|public
name|void
name|testDaemonStream
parameter_list|()
throws|throws
name|Exception
block|{
name|DaemonStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|DaemonStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"daemon(search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\"), id=\"blah\", runInterval=\"1000\", queueSize=\"100\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"daemon"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DaemonStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTopicStream
specifier|public
name|void
name|testTopicStream
parameter_list|()
throws|throws
name|Exception
block|{
name|TopicStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|TopicStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"topic(collection2, collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", id=\"blah\", checkpointEvery=1000)"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"topic"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TopicStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatsStream
specifier|public
name|void
name|testStatsStream
parameter_list|()
throws|throws
name|Exception
block|{
name|StatsStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|StatsStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"stats(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\", sum(a_i), avg(a_i), count(*), min(a_i), max(a_i))"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"stats"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|StatsStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUniqueStream
specifier|public
name|void
name|testUniqueStream
parameter_list|()
throws|throws
name|Exception
block|{
name|UniqueStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|UniqueStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"unique(search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_i asc\"), over=\"a_f\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"unique"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|UniqueStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMergeStream
specifier|public
name|void
name|testMergeStream
parameter_list|()
throws|throws
name|Exception
block|{
name|MergeStream
name|stream
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|MergeStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"merge("
operator|+
literal|"search(collection1, q=\"id:(0 3 4)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"search(collection1, q=\"id:(1 2)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"on=\"a_f asc, a_s asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"merge"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MergeStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRankStream
specifier|public
name|void
name|testRankStream
parameter_list|()
throws|throws
name|Exception
block|{
name|RankStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|RankStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"top("
operator|+
literal|"n=3,"
operator|+
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc,a_i asc\"),"
operator|+
literal|"sort=\"a_f asc, a_i asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"top"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RankStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReducerStream
specifier|public
name|void
name|testReducerStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ReducerStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|ReducerStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"reduce("
operator|+
literal|"search(collection1, q=*:*, fl=\"id,a_s,a_i,a_f\", sort=\"a_s desc, a_f asc\"),"
operator|+
literal|"by=\"a_s\", group(sort=\"a_i desc\", n=\"5\"))"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"reduce"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ReducerStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateStream
specifier|public
name|void
name|testUpdateStream
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamExpression
name|expression
init|=
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"update("
operator|+
literal|"collection2, "
operator|+
literal|"batchSize=5, "
operator|+
literal|"search("
operator|+
literal|"collection1, "
operator|+
literal|"q=*:*, "
operator|+
literal|"fl=\"id,a_s,a_i,a_f\", "
operator|+
literal|"sort=\"a_f asc, a_i asc\"))"
argument_list|)
decl_stmt|;
name|UpdateStream
name|updateStream
init|=
operator|new
name|UpdateStream
argument_list|(
name|expression
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|Explanation
name|explanation
init|=
name|updateStream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr (collection2)"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Solr/Lucene"
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|StreamExplanation
name|updateExplanation
init|=
operator|(
name|StreamExplanation
operator|)
name|explanation
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|updateExplanation
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"update"
argument_list|,
name|updateExplanation
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|UpdateStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|updateExplanation
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFacetStream
specifier|public
name|void
name|testFacetStream
parameter_list|()
throws|throws
name|Exception
block|{
name|FacetStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|FacetStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"facet("
operator|+
literal|"collection1, "
operator|+
literal|"q=\"*:*\", "
operator|+
literal|"buckets=\"a_s\", "
operator|+
literal|"bucketSorts=\"sum(a_i) asc\", "
operator|+
literal|"bucketSizeLimit=100, "
operator|+
literal|"sum(a_i), sum(a_f), "
operator|+
literal|"min(a_i), min(a_f), "
operator|+
literal|"max(a_i), max(a_f), "
operator|+
literal|"avg(a_i), avg(a_f), "
operator|+
literal|"count(*)"
operator|+
literal|")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|expressionString
operator|=
name|stream
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"facet"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FacetStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJDBCStream
specifier|public
name|void
name|testJDBCStream
parameter_list|()
throws|throws
name|Exception
block|{
name|JDBCStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|JDBCStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"jdbc(connection=\"jdbc:hsqldb:mem:.\", sql=\"select PEOPLE.ID, PEOPLE.NAME, COUNTRIES.COUNTRY_NAME from PEOPLE inner join COUNTRIES on PEOPLE.COUNTRY_CODE = COUNTRIES.CODE order by PEOPLE.ID\", sort=\"ID asc\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"jdbc"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JDBCStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersectStream
specifier|public
name|void
name|testIntersectStream
parameter_list|()
throws|throws
name|Exception
block|{
name|IntersectStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|IntersectStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"intersect("
operator|+
literal|"search(collection1, q=\"id:(0 3 4)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"search(collection1, q=\"id:(1 2)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"on=\"a_f, a_s\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"intersect"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|IntersectStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComplementStream
specifier|public
name|void
name|testComplementStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ComplementStream
name|stream
decl_stmt|;
name|String
name|expressionString
decl_stmt|;
comment|// Basic test
name|stream
operator|=
operator|new
name|ComplementStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
literal|"complement("
operator|+
literal|"search(collection1, q=\"id:(0 3 4)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"search(collection1, q=\"id:(1 2)\", fl=\"id,a_s,a_i,a_f\", sort=\"a_f asc, a_s asc\"),"
operator|+
literal|"on=\"a_f, a_s\")"
argument_list|)
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|Explanation
name|explanation
init|=
name|stream
operator|.
name|toExplanation
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"complement"
argument_list|,
name|explanation
operator|.
name|getFunctionName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ComplementStream
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|explanation
operator|.
name|getImplementingClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|StreamExplanation
operator|)
name|explanation
operator|)
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

