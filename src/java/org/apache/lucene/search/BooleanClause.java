begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** A clause in a BooleanQuery. */
end_comment

begin_class
DECL|class|BooleanClause
specifier|public
class|class
name|BooleanClause
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** The query whose matching documents are combined by the boolean query. */
DECL|field|query
specifier|public
name|Query
name|query
decl_stmt|;
comment|/** If true, documents documents which<i>do not</i>     match this sub-query will<i>not</i> match the boolean query. */
DECL|field|required
specifier|public
name|boolean
name|required
init|=
literal|false
decl_stmt|;
comment|/** If true, documents documents which<i>do</i>     match this sub-query will<i>not</i> match the boolean query. */
DECL|field|prohibited
specifier|public
name|boolean
name|prohibited
init|=
literal|false
decl_stmt|;
comment|/** Constructs a BooleanClause with query<code>q</code>, required<code>r</code> and prohibited<code>p</code>. */
DECL|method|BooleanClause
specifier|public
name|BooleanClause
parameter_list|(
name|Query
name|q
parameter_list|,
name|boolean
name|r
parameter_list|,
name|boolean
name|p
parameter_list|)
block|{
name|query
operator|=
name|q
expr_stmt|;
name|required
operator|=
name|r
expr_stmt|;
name|prohibited
operator|=
name|p
expr_stmt|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
operator|!
operator|(
name|o
operator|instanceof
name|BooleanClause
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanClause
name|other
init|=
operator|(
name|BooleanClause
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|required
operator|==
name|other
operator|.
name|required
operator|)
operator|&&
operator|(
name|this
operator|.
name|prohibited
operator|==
name|other
operator|.
name|prohibited
operator|)
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|this
operator|.
name|required
condition|?
literal|1
else|:
literal|0
operator|)
operator|^
operator|(
name|this
operator|.
name|prohibited
condition|?
literal|2
else|:
literal|0
operator|)
return|;
block|}
block|}
end_class

end_unit

