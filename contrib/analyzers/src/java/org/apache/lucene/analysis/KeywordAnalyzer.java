begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * "Tokenizes" the entire stream as a single token.  */
end_comment

begin_class
DECL|class|KeywordAnalyzer
specifier|public
class|class
name|KeywordAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
name|boolean
name|done
decl_stmt|;
specifier|private
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|length
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|length
operator|=
name|reader
operator|.
name|read
argument_list|(
name|this
operator|.
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|buffer
operator|.
name|append
argument_list|(
name|this
operator|.
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|String
name|text
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|Token
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

