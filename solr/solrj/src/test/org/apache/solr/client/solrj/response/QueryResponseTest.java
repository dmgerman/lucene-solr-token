begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DateUtil
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
name|SolrResourceLoader
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
name|Reader
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

begin_comment
comment|/**  * A few tests for parsing Solr response in QueryResponse  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|QueryResponseTest
specifier|public
class|class
name|QueryResponseTest
extends|extends
name|LuceneTestCase
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
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/sampleDateFacetResponse.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Reader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
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
annotation|@
name|Test
DECL|method|testRangeFacets
specifier|public
name|void
name|testRangeFacets
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
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/sampleDateFacetResponse.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Reader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
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
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|RangeFacet
operator|.
name|Numeric
name|price
init|=
literal|null
decl_stmt|;
name|RangeFacet
operator|.
name|Date
name|manufacturedateDt
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RangeFacet
name|r
range|:
name|qr
operator|.
name|getFacetRanges
argument_list|()
control|)
block|{
name|assertNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"price"
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|price
operator|=
operator|(
name|RangeFacet
operator|.
name|Numeric
operator|)
name|r
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"manufacturedate_dt"
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|manufacturedateDt
operator|=
operator|(
name|RangeFacet
operator|.
name|Date
operator|)
name|r
expr_stmt|;
block|}
name|counter
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|price
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|manufacturedateDt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0F
argument_list|,
name|price
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0F
argument_list|,
name|price
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|price
operator|.
name|getGap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0.0"
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2.0"
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3.0"
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.0"
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|price
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DateUtil
operator|.
name|parseDate
argument_list|(
literal|"2005-02-13T15:26:37Z"
argument_list|)
argument_list|,
name|manufacturedateDt
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DateUtil
operator|.
name|parseDate
argument_list|(
literal|"2008-02-13T15:26:37Z"
argument_list|)
argument_list|,
name|manufacturedateDt
operator|.
name|getEnd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+1YEAR"
argument_list|,
name|manufacturedateDt
operator|.
name|getGap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2005-02-13T15:26:37Z"
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2006-02-13T15:26:37Z"
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2007-02-13T15:26:37Z"
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|manufacturedateDt
operator|.
name|getCounts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|manufacturedateDt
operator|.
name|getBefore
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|manufacturedateDt
operator|.
name|getAfter
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|manufacturedateDt
operator|.
name|getBetween
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGroupResponse
specifier|public
name|void
name|testGroupResponse
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
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/sampleGroupResponse.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Reader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
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
name|assertNotNull
argument_list|(
name|qr
argument_list|)
expr_stmt|;
name|GroupResponse
name|groupResponse
init|=
name|qr
operator|.
name|getGroupResponse
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|groupResponse
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|GroupCommand
argument_list|>
name|commands
init|=
name|groupResponse
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|commands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|GroupCommand
name|fieldCommand
init|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"acco_id"
argument_list|,
name|fieldCommand
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30000000
argument_list|,
name|fieldCommand
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5687
argument_list|,
name|fieldCommand
operator|.
name|getNGroups
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|fieldCommandGroups
init|=
name|fieldCommand
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|fieldCommandGroups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"116_ar"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2236
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"116_hi"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2234
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"953_ar"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1020
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"953_hi"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1030
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"954_ar"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2236
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"954_hi"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2234
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"546_ar"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4984
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"546_hi"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4984
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"708_ar"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4627
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"708_hi"
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4627
argument_list|,
name|fieldCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|GroupCommand
name|funcCommand
init|=
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"sum(price, price)"
argument_list|,
name|funcCommand
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30000000
argument_list|,
name|funcCommand
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|funcCommand
operator|.
name|getNGroups
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|funcCommandGroups
init|=
name|funcCommand
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|funcCommandGroups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"95000.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|43666
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"91400.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|27120
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"104800.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|34579
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"99400.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40519
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"109600.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|36203
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"102400.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|37852
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"116800.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40393
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"107800.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|41639
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"136200.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|25929
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"131400.0"
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|29179
argument_list|,
name|funcCommandGroups
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|GroupCommand
name|queryCommand
init|=
name|commands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"country:fr"
argument_list|,
name|queryCommand
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|queryCommand
operator|.
name|getNGroups
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30000000
argument_list|,
name|queryCommand
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Group
argument_list|>
name|queryCommandGroups
init|=
name|queryCommand
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queryCommandGroups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"country:fr"
argument_list|,
name|queryCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroupValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queryCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57074
argument_list|,
name|queryCommandGroups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResult
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

