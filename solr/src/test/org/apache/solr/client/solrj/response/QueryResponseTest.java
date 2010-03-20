begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
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
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|FileReader
import|;
end_import

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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Simple test for Date facet support in QueryResponse  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|QueryResponseTest
specifier|public
class|class
name|QueryResponseTest
block|{
annotation|@
name|Test
DECL|method|testDateFacets
specifier|public
name|void
name|testDateFacets
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResponseParser
name|parser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
name|FileReader
name|in
init|=
operator|new
name|FileReader
argument_list|(
literal|"sampleDateFacetResponse.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"in is null and it shouldn't be"
argument_list|,
name|in
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|parser
operator|.
name|processResponse
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueryResponse
name|qr
init|=
operator|new
name|QueryResponse
argument_list|(
name|response
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|qr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|qr
operator|.
name|getFacetDates
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetField
name|f
range|:
name|qr
operator|.
name|getFacetDates
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// TODO - test values?
comment|// System.out.println(f.toString());
comment|// System.out.println("GAP: " + f.getGap());
comment|// System.out.println("END: " + f.getEnd());
block|}
block|}
block|}
end_class

end_unit

