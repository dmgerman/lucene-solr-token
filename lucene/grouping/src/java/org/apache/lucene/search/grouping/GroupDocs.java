begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|search
operator|.
name|ScoreDoc
import|;
end_import

begin_comment
comment|/** Represents one group in the results.  *   * @lucene.experimental */
end_comment

begin_class
DECL|class|GroupDocs
specifier|public
class|class
name|GroupDocs
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
block|{
comment|/** The groupField value for all docs in this group; this    *  may be null if hits did not have the groupField. */
DECL|field|groupValue
specifier|public
specifier|final
name|GROUP_VALUE_TYPE
name|groupValue
decl_stmt|;
comment|/** Max score in this group */
DECL|field|maxScore
specifier|public
specifier|final
name|float
name|maxScore
decl_stmt|;
comment|/** Overall aggregated score of this group (currently only    *  set by join queries). */
DECL|field|score
specifier|public
specifier|final
name|float
name|score
decl_stmt|;
comment|/** Hits; this may be {@link    * org.apache.lucene.search.FieldDoc} instances if the    * withinGroupSort sorted by fields. */
DECL|field|scoreDocs
specifier|public
specifier|final
name|ScoreDoc
index|[]
name|scoreDocs
decl_stmt|;
comment|/** Total hits within this group */
DECL|field|totalHits
specifier|public
specifier|final
name|int
name|totalHits
decl_stmt|;
comment|/** Matches the groupSort passed to {@link    *  AbstractFirstPassGroupingCollector}. */
DECL|field|groupSortValues
specifier|public
specifier|final
name|Object
index|[]
name|groupSortValues
decl_stmt|;
DECL|method|GroupDocs
specifier|public
name|GroupDocs
parameter_list|(
name|float
name|score
parameter_list|,
name|float
name|maxScore
parameter_list|,
name|int
name|totalHits
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|GROUP_VALUE_TYPE
name|groupValue
parameter_list|,
name|Object
index|[]
name|groupSortValues
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
name|this
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|groupSortValues
operator|=
name|groupSortValues
expr_stmt|;
block|}
block|}
end_class

end_unit

