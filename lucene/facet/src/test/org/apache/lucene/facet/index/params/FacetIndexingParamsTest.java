begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|params
package|;
end_package

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
name|FacetTestCase
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
name|DrillDown
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
name|taxonomy
operator|.
name|CategoryPath
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
name|util
operator|.
name|PartitionsUtils
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
name|Term
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FacetIndexingParamsTest
specifier|public
class|class
name|FacetIndexingParamsTest
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testDefaultSettings
specifier|public
name|void
name|testDefaultSettings
parameter_list|()
block|{
name|FacetIndexingParams
name|dfip
init|=
name|FacetIndexingParams
operator|.
name|ALL_PARENTS
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Missing default category list"
argument_list|,
name|dfip
operator|.
name|getAllCategoryListParams
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"all categories have the same CategoryListParams by default"
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected default category list field is $facets"
argument_list|,
literal|"$facets"
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
operator|.
name|field
argument_list|)
expr_stmt|;
name|String
name|expectedDDText
init|=
literal|"a"
operator|+
name|dfip
operator|.
name|getFacetDelimChar
argument_list|()
operator|+
literal|"b"
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong drill-down term"
argument_list|,
operator|new
name|Term
argument_list|(
literal|"$facets"
argument_list|,
name|expectedDDText
argument_list|)
argument_list|,
name|DrillDown
operator|.
name|term
argument_list|(
name|dfip
argument_list|,
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|20
index|]
decl_stmt|;
name|int
name|numchars
init|=
name|dfip
operator|.
name|drillDownTermText
argument_list|(
name|cp
argument_list|,
name|buf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"3 characters should be written"
argument_list|,
literal|3
argument_list|,
name|numchars
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong drill-down term text"
argument_list|,
name|expectedDDText
argument_list|,
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|numchars
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"partition for all ordinals is the first"
argument_list|,
literal|""
argument_list|,
name|PartitionsUtils
operator|.
name|partitionNameByOrdinal
argument_list|(
name|dfip
argument_list|,
literal|250
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"for partition 0, the same name should be returned"
argument_list|,
literal|""
argument_list|,
name|PartitionsUtils
operator|.
name|partitionName
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"for any other, it's the concatenation of name + partition"
argument_list|,
name|PartitionsUtils
operator|.
name|PART_NAME_PREFIX
operator|+
literal|"1"
argument_list|,
name|PartitionsUtils
operator|.
name|partitionName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default partition number is always 0"
argument_list|,
literal|0
argument_list|,
name|PartitionsUtils
operator|.
name|partitionNumber
argument_list|(
name|dfip
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default partition size is unbounded"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|dfip
operator|.
name|getPartitionSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCategoryListParamsWithDefaultIndexingParams
specifier|public
name|void
name|testCategoryListParamsWithDefaultIndexingParams
parameter_list|()
block|{
name|CategoryListParams
name|clp
init|=
operator|new
name|CategoryListParams
argument_list|(
literal|"clp"
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|dfip
init|=
operator|new
name|FacetIndexingParams
argument_list|(
name|clp
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected default category list field is "
operator|+
name|clp
operator|.
name|field
argument_list|,
name|clp
operator|.
name|field
argument_list|,
name|dfip
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

