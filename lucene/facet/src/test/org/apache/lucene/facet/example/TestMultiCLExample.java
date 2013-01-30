begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.example
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|Test
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
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|multiCL
operator|.
name|MultiCLMain
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
name|search
operator|.
name|results
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Test that the multi-category list example works as expected. This test helps  * to verify that examples code is alive!  */
end_comment

begin_class
DECL|class|TestMultiCLExample
specifier|public
class|class
name|TestMultiCLExample
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testMulti
specifier|public
name|void
name|testMulti
parameter_list|()
throws|throws
name|Exception
block|{
name|ExampleResult
name|res
init|=
operator|new
name|MultiCLMain
argument_list|()
operator|.
name|runSample
argument_list|()
decl_stmt|;
name|assertCorrectMultiResults
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCorrectMultiResults
specifier|public
specifier|static
name|void
name|assertCorrectMultiResults
parameter_list|(
name|ExampleResult
name|exampleResults
parameter_list|)
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|exampleResults
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|FacetResult
name|result
init|=
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Result should not be null"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|FacetResultNode
name|node
init|=
name|result
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid label"
argument_list|,
literal|"5"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid # of subresults"
argument_list|,
literal|3
argument_list|,
name|node
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|FacetResultNode
argument_list|>
name|subResults
init|=
name|node
operator|.
name|subResults
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|FacetResultNode
name|sub
init|=
name|subResults
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult value"
argument_list|,
literal|1.0
argument_list|,
name|sub
operator|.
name|value
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult label"
argument_list|,
literal|"5/2"
argument_list|,
name|sub
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sub
operator|=
name|subResults
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult value"
argument_list|,
literal|1.0
argument_list|,
name|sub
operator|.
name|value
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult label"
argument_list|,
literal|"5/7"
argument_list|,
name|sub
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sub
operator|=
name|subResults
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult value"
argument_list|,
literal|1.0
argument_list|,
name|sub
operator|.
name|value
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid subresult label"
argument_list|,
literal|"5/5"
argument_list|,
name|sub
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|node
operator|=
name|result
operator|.
name|getFacetResultNode
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Result should not be null"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid label"
argument_list|,
literal|"5/5"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid value"
argument_list|,
literal|1
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid number of subresults"
argument_list|,
literal|0
argument_list|,
name|node
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|node
operator|=
name|result
operator|.
name|getFacetResultNode
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Result should not be null"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid label"
argument_list|,
literal|"6/2"
argument_list|,
name|node
operator|.
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid value"
argument_list|,
literal|1
argument_list|,
name|node
operator|.
name|value
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid number of subresults"
argument_list|,
literal|0
argument_list|,
name|node
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

