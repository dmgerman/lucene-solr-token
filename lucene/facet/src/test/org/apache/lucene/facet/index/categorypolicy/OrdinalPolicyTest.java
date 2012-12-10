begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.categorypolicy
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
name|categorypolicy
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
name|taxonomy
operator|.
name|TaxonomyReader
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
name|TaxonomyWriter
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|OrdinalPolicyTest
specifier|public
class|class
name|OrdinalPolicyTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testDefaultOrdinalPolicy
specifier|public
name|void
name|testDefaultOrdinalPolicy
parameter_list|()
block|{
comment|// check ordinal policy
name|OrdinalPolicy
name|ordinalPolicy
init|=
name|OrdinalPolicy
operator|.
name|ALL_PARENTS
decl_stmt|;
name|assertFalse
argument_list|(
literal|"default ordinal policy should not match root"
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"default ordinal policy should match "
operator|+
name|ordinal
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|ordinal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNonTopLevelOrdinalPolicy
specifier|public
name|void
name|testNonTopLevelOrdinalPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomy
init|=
literal|null
decl_stmt|;
name|taxonomy
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|int
index|[]
name|topLevelOrdinals
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
name|String
index|[]
name|topLevelStrings
init|=
operator|new
name|String
index|[
literal|10
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|topLevelStrings
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|topLevelOrdinals
index|[
name|i
index|]
operator|=
name|taxonomy
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|topLevelStrings
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|nonTopLevelOrdinals
init|=
operator|new
name|int
index|[
literal|300
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nComponents
init|=
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
index|[]
name|components
init|=
operator|new
name|String
index|[
name|nComponents
index|]
decl_stmt|;
name|components
index|[
literal|0
index|]
operator|=
name|topLevelStrings
index|[
name|i
operator|%
literal|10
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|components
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|components
index|[
name|j
index|]
operator|=
operator|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|nonTopLevelOrdinals
index|[
name|i
index|]
operator|=
name|taxonomy
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check ordinal policy
name|OrdinalPolicy
name|ordinalPolicy
init|=
operator|new
name|NonTopLevelOrdinalPolicy
argument_list|()
decl_stmt|;
name|ordinalPolicy
operator|.
name|init
argument_list|(
name|taxonomy
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"top level ordinal policy should not match root"
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
literal|"top level ordinal policy should not match "
operator|+
name|topLevelOrdinals
index|[
name|i
index|]
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|topLevelOrdinals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"top level ordinal policy should match "
operator|+
name|nonTopLevelOrdinals
index|[
name|i
index|]
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
name|nonTopLevelOrdinals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check illegal ordinal
name|assertFalse
argument_list|(
literal|"Should not add illegal ordinal"
argument_list|,
name|ordinalPolicy
operator|.
name|shouldAdd
argument_list|(
literal|100000
argument_list|)
argument_list|)
expr_stmt|;
name|taxonomy
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

