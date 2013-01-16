begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|util
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Utilities for partitions - sizes and such  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PartitionsUtils
specifier|public
specifier|final
class|class
name|PartitionsUtils
block|{
comment|/** The prefix that is added to the name of the partition. */
DECL|field|PART_NAME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PART_NAME_PREFIX
init|=
literal|"$part"
decl_stmt|;
comment|/**    * Get the partition size in this parameter, or return the size of the taxonomy, which    * is smaller.  (Guarantees usage of as little memory as possible at search time).    */
DECL|method|partitionSize
specifier|public
specifier|final
specifier|static
name|int
name|partitionSize
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|,
specifier|final
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
argument_list|,
name|taxonomyReader
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Partition number of an ordinal.    *<p>    * This allows to locate the partition containing a certain (facet) ordinal.    * @see FacetIndexingParams#getPartitionSize()          */
DECL|method|partitionNumber
specifier|public
specifier|final
specifier|static
name|int
name|partitionNumber
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|ordinal
operator|/
name|iParams
operator|.
name|getPartitionSize
argument_list|()
return|;
block|}
comment|/**    * Partition name by category ordinal    */
DECL|method|partitionNameByOrdinal
specifier|public
specifier|final
specifier|static
name|String
name|partitionNameByOrdinal
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|int
name|partition
init|=
name|partitionNumber
argument_list|(
name|iParams
argument_list|,
name|ordinal
argument_list|)
decl_stmt|;
return|return
name|partitionName
argument_list|(
name|partition
argument_list|)
return|;
block|}
comment|/** Partition name by its number */
DECL|method|partitionName
specifier|public
specifier|final
specifier|static
name|String
name|partitionName
parameter_list|(
name|int
name|partition
parameter_list|)
block|{
comment|// TODO would be good if this method isn't called when partitions are not enabled.
comment|// perhaps through some specialization code.
if|if
condition|(
name|partition
operator|==
literal|0
condition|)
block|{
comment|// since regular faceted search code goes through this method too,
comment|// return the same value for partition 0 and when there are no partitions
return|return
literal|""
return|;
block|}
return|return
name|PART_NAME_PREFIX
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|partition
argument_list|)
return|;
block|}
block|}
end_class

end_unit

