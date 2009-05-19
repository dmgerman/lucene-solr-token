begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|solr
operator|.
name|search
operator|.
name|SolrIndexReader
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
name|IndexReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A value source that wraps another and ensures that the top level reader  * is used.  This is useful for value sources like ord() who's value depend  * on all those around it.  */
end_comment

begin_class
DECL|class|TopValueSource
specifier|public
class|class
name|TopValueSource
extends|extends
name|ValueSource
block|{
DECL|field|vs
specifier|private
specifier|final
name|ValueSource
name|vs
decl_stmt|;
DECL|method|TopValueSource
specifier|public
name|TopValueSource
parameter_list|(
name|ValueSource
name|vs
parameter_list|)
block|{
name|this
operator|.
name|vs
operator|=
name|vs
expr_stmt|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|vs
return|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"top("
operator|+
name|vs
operator|.
name|description
argument_list|()
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|IndexReader
name|topReader
init|=
name|reader
decl_stmt|;
if|if
condition|(
name|topReader
operator|instanceof
name|SolrIndexReader
condition|)
block|{
name|SolrIndexReader
name|r
init|=
operator|(
name|SolrIndexReader
operator|)
name|topReader
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|offset
operator|+=
name|r
operator|.
name|getBase
argument_list|()
expr_stmt|;
name|r
operator|=
name|r
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|topReader
operator|=
name|r
expr_stmt|;
block|}
specifier|final
name|int
name|off
init|=
name|offset
decl_stmt|;
specifier|final
name|DocValues
name|vals
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|topReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|topReader
operator|==
name|reader
condition|)
return|return
name|vals
return|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|intVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|longVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|doubleVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|strVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|vals
operator|.
name|strVal
argument_list|(
name|doc
operator|+
name|off
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|TopValueSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|TopValueSource
name|other
init|=
operator|(
name|TopValueSource
operator|)
name|o
decl_stmt|;
return|return
name|vs
operator|.
name|equals
argument_list|(
name|other
operator|.
name|vs
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|vs
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|31
operator|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"top("
operator|+
name|vs
operator|.
name|toString
argument_list|()
operator|+
literal|')'
return|;
block|}
block|}
end_class

end_unit

