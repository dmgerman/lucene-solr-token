begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|QueryUtils
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
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|// NOTE: this is a direct result of SOLR-2829
end_comment

begin_class
DECL|class|TestValueSourceCache
specifier|public
class|class
name|TestValueSourceCache
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|_func
operator|=
name|QParser
operator|.
name|getParser
argument_list|(
literal|null
argument_list|,
name|FunctionQParserPlugin
operator|.
name|NAME
argument_list|,
name|lrf
operator|.
name|makeRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|_func
specifier|static
name|QParser
name|_func
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|_func
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getQuery
name|Query
name|getQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|ParseException
block|{
name|_func
operator|.
name|setString
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|_func
operator|.
name|parse
argument_list|()
return|;
block|}
comment|// This is actually also tested by the tests for val_d1 below, but the bug was reported against geodist()...
annotation|@
name|Test
DECL|method|testGeodistSource
specifier|public
name|void
name|testGeodistSource
parameter_list|()
throws|throws
name|ParseException
block|{
name|Query
name|q_home
init|=
name|getQuery
argument_list|(
literal|"geodist(home_ll, 45.0, 43.0)"
argument_list|)
decl_stmt|;
name|Query
name|q_work
init|=
name|getQuery
argument_list|(
literal|"geodist(work_ll, 45.0, 43.0)"
argument_list|)
decl_stmt|;
name|Query
name|q_home2
init|=
name|getQuery
argument_list|(
literal|"geodist(home_ll, 45.0, 43.0)"
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q_work
argument_list|,
name|q_home
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q_home
argument_list|,
name|q_home2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|templates
init|=
operator|new
name|String
index|[]
block|{
literal|"sum(#v0, #n0)"
block|,
literal|"product(pow(#v0,#n0),#v1,#n1)"
block|,
literal|"log(#v0)"
block|,
literal|"log(sum(#n0,#v0,#v1,#n1))"
block|,
literal|"scale(map(#v0,#n0,#n1,#n2),#n3,#n4)"
block|,     }
decl_stmt|;
name|String
index|[]
name|numbers
init|=
operator|new
name|String
index|[]
block|{
literal|"1,2,3,4,5"
block|,
literal|"1.0,2.0,3.0,4.0,5.0"
block|,
literal|"1,2.0,3,4.0,5"
block|,
literal|"1.0,2,3.0,4,5.0"
block|,
literal|"1000000,2000000,3000000,4000000,5000000"
block|}
decl_stmt|;
name|String
index|[]
name|types
init|=
operator|new
name|String
index|[]
block|{
literal|"val1_f1"
block|,
literal|"val1_d1"
block|,
literal|"val1_b1"
block|,
literal|"val1_i1"
block|,
literal|"val1_l1"
block|,
literal|"val1_b1"
block|,
literal|"val1_by1"
block|,
literal|"val1_sh1"
block|}
decl_stmt|;
for|for
control|(
name|String
name|template
range|:
name|templates
control|)
block|{
for|for
control|(
name|String
name|nums
range|:
name|numbers
control|)
block|{
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|tryQuerySameTypes
argument_list|(
name|template
argument_list|,
name|nums
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|tryQueryDiffTypes
argument_list|(
name|template
argument_list|,
name|nums
argument_list|,
name|types
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// This test should will fail because q1 and q3 evaluate as equal unless
comment|// fixes for bug 2829 are in place.
DECL|method|tryQuerySameTypes
name|void
name|tryQuerySameTypes
parameter_list|(
name|String
name|template
parameter_list|,
name|String
name|numbers
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|s1
init|=
name|template
decl_stmt|;
name|String
name|s2
init|=
name|template
decl_stmt|;
name|String
name|s3
init|=
name|template
decl_stmt|;
name|String
index|[]
name|numParts
init|=
name|numbers
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|type2
init|=
name|type
operator|.
name|replace
argument_list|(
literal|"val1"
argument_list|,
literal|"val2"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|s1
operator|.
name|contains
argument_list|(
literal|"#"
argument_list|)
condition|;
operator|++
name|idx
control|)
block|{
name|String
name|patV
init|=
literal|"#v"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|String
name|patN
init|=
literal|"#n"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|s1
operator|=
name|s1
operator|.
name|replace
argument_list|(
name|patV
argument_list|,
name|type
argument_list|)
operator|.
name|replace
argument_list|(
name|patN
argument_list|,
name|numParts
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
name|s2
operator|=
name|s2
operator|.
name|replace
argument_list|(
name|patV
argument_list|,
name|type
argument_list|)
operator|.
name|replace
argument_list|(
name|patN
argument_list|,
name|numParts
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
name|s3
operator|=
name|s3
operator|.
name|replace
argument_list|(
name|patV
argument_list|,
name|type2
argument_list|)
operator|.
name|replace
argument_list|(
name|patN
argument_list|,
name|numParts
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
comment|//SolrQueryRequest req1 = req( "q","*:*", "fq", s1);
name|Query
name|q1
init|=
name|getQuery
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|getQuery
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|Query
name|q3
init|=
name|getQuery
argument_list|(
name|s3
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q3
argument_list|)
expr_stmt|;
block|}
comment|// These should always and forever fail, and would have failed without the fixes for 2829, but why not make
comment|// some more tests just in case???
DECL|method|tryQueryDiffTypes
name|void
name|tryQueryDiffTypes
parameter_list|(
name|String
name|template
parameter_list|,
name|String
name|numbers
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|s1
init|=
name|template
decl_stmt|;
name|String
name|s2
init|=
name|template
decl_stmt|;
name|String
index|[]
name|numParts
init|=
name|numbers
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|s1
operator|.
name|contains
argument_list|(
literal|"#"
argument_list|)
condition|;
operator|++
name|idx
control|)
block|{
name|String
name|patV
init|=
literal|"#v"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|String
name|patN
init|=
literal|"#n"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|s1
operator|=
name|s1
operator|.
name|replace
argument_list|(
name|patV
argument_list|,
name|types
index|[
name|idx
operator|%
name|types
operator|.
name|length
index|]
argument_list|)
operator|.
name|replace
argument_list|(
name|patN
argument_list|,
name|numParts
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
name|s2
operator|=
name|s2
operator|.
name|replace
argument_list|(
name|patV
argument_list|,
name|types
index|[
operator|(
name|idx
operator|+
literal|1
operator|)
operator|%
name|types
operator|.
name|length
index|]
argument_list|)
operator|.
name|replace
argument_list|(
name|patN
argument_list|,
name|numParts
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
name|Query
name|q1
init|=
name|getQuery
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
name|getQuery
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

