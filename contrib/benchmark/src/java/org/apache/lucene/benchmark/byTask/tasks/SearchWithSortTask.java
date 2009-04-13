begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|QueryMaker
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
name|Sort
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
name|SortField
import|;
end_import

begin_comment
comment|/**  * Does sort search on specified field.  *   */
end_comment

begin_class
DECL|class|SearchWithSortTask
specifier|public
class|class
name|SearchWithSortTask
extends|extends
name|ReadTask
block|{
DECL|field|doScore
specifier|private
name|boolean
name|doScore
init|=
literal|true
decl_stmt|;
DECL|field|doMaxScore
specifier|private
name|boolean
name|doMaxScore
init|=
literal|true
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|method|SearchWithSortTask
specifier|public
name|SearchWithSortTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
comment|/**    * SortFields: field:type,field:type[,noscore][,nomaxscore]    *    * If noscore is present, then we turn off score tracking    * in {@link org.apache.lucene.search.TopFieldCollector}.    * If nomaxscore is present, then we turn off maxScore tracking    * in {@link org.apache.lucene.search.TopFieldCollector}.    *     * name,byline:int,subject:auto    *     */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|sortField
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|sortField
argument_list|)
expr_stmt|;
name|String
index|[]
name|fields
init|=
name|sortField
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|SortField
index|[]
name|sortFields
init|=
operator|new
name|SortField
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|SortField
name|sortField0
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"doc"
argument_list|)
condition|)
block|{
name|sortField0
operator|=
name|SortField
operator|.
name|FIELD_DOC
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"noscore"
argument_list|)
condition|)
block|{
name|doScore
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"nomaxscore"
argument_list|)
condition|)
block|{
name|doMaxScore
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|int
name|index
init|=
name|field
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|fieldName
decl_stmt|;
name|String
name|typeString
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|fieldName
operator|=
name|field
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|typeString
operator|=
name|field
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|index
argument_list|,
name|field
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typeString
operator|=
literal|"auto"
expr_stmt|;
name|fieldName
operator|=
name|field
expr_stmt|;
block|}
name|int
name|type
init|=
name|getType
argument_list|(
name|typeString
argument_list|)
decl_stmt|;
name|sortField0
operator|=
operator|new
name|SortField
argument_list|(
name|fieldName
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|sortFields
index|[
name|upto
operator|++
index|]
operator|=
name|sortField0
expr_stmt|;
block|}
if|if
condition|(
name|upto
operator|<
name|sortFields
operator|.
name|length
condition|)
block|{
name|SortField
index|[]
name|newSortFields
init|=
operator|new
name|SortField
index|[
name|upto
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|sortFields
argument_list|,
literal|0
argument_list|,
name|newSortFields
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|sortFields
operator|=
name|newSortFields
expr_stmt|;
block|}
name|this
operator|.
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sortFields
argument_list|)
expr_stmt|;
block|}
DECL|method|getType
specifier|private
name|int
name|getType
parameter_list|(
name|String
name|typeString
parameter_list|)
block|{
name|int
name|type
decl_stmt|;
if|if
condition|(
name|typeString
operator|.
name|equals
argument_list|(
literal|"float"
argument_list|)
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|FLOAT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|typeString
operator|.
name|equals
argument_list|(
literal|"int"
argument_list|)
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|INT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|typeString
operator|.
name|equals
argument_list|(
literal|"string"
argument_list|)
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|STRING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|typeString
operator|.
name|equals
argument_list|(
literal|"string_val"
argument_list|)
condition|)
block|{
name|type
operator|=
name|SortField
operator|.
name|STRING_VAL
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|SortField
operator|.
name|AUTO
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|getQueryMaker
specifier|public
name|QueryMaker
name|getQueryMaker
parameter_list|()
block|{
return|return
name|getRunData
argument_list|()
operator|.
name|getQueryMaker
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|withSearch
specifier|public
name|boolean
name|withSearch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|withTraverse
specifier|public
name|boolean
name|withTraverse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|withWarm
specifier|public
name|boolean
name|withWarm
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|withScore
specifier|public
name|boolean
name|withScore
parameter_list|()
block|{
return|return
name|doScore
return|;
block|}
DECL|method|withMaxScore
specifier|public
name|boolean
name|withMaxScore
parameter_list|()
block|{
return|return
name|doMaxScore
return|;
block|}
DECL|method|getSort
specifier|public
name|Sort
name|getSort
parameter_list|()
block|{
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No sort field was set"
argument_list|)
throw|;
block|}
return|return
name|sort
return|;
block|}
block|}
end_class

end_unit

