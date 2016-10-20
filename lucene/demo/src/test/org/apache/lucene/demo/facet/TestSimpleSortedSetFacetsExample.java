begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package

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
name|facet
operator|.
name|FacetResult
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|// We require sorted set DVs:
end_comment

begin_class
DECL|class|TestSimpleSortedSetFacetsExample
specifier|public
class|class
name|TestSimpleSortedSetFacetsExample
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|SimpleSortedSetFacetsExample
argument_list|()
operator|.
name|runSearch
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Author path=[] value=5 childCount=4\n  Lisa (2)\n  Bob (1)\n  Frank (1)\n  Susan (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Publish Year path=[] value=5 childCount=3\n  2010 (2)\n  2012 (2)\n  1999 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDrillDown
specifier|public
name|void
name|testDrillDown
parameter_list|()
throws|throws
name|Exception
block|{
name|FacetResult
name|result
init|=
operator|new
name|SimpleSortedSetFacetsExample
argument_list|()
operator|.
name|runDrillDown
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Author path=[] value=2 childCount=2\n  Bob (1)\n  Lisa (1)\n"
argument_list|,
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

