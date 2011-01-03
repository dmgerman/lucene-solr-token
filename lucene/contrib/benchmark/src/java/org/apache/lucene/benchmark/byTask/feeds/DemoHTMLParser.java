begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
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
name|feeds
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * HTML Parser that is based on Lucene's demo HTML parser.  */
end_comment

begin_class
DECL|class|DemoHTMLParser
specifier|public
class|class
name|DemoHTMLParser
implements|implements
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
name|HTMLParser
block|{
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|DateFormat
name|dateFormat
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|html
operator|.
name|HTMLParser
name|p
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|html
operator|.
name|HTMLParser
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// title
name|String
name|title
init|=
name|p
operator|.
name|getTitle
argument_list|()
decl_stmt|;
comment|// properties
name|Properties
name|props
init|=
name|p
operator|.
name|getMetaTags
argument_list|()
decl_stmt|;
comment|// body
name|Reader
name|r
init|=
name|p
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|char
name|c
index|[]
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|StringBuilder
name|bodyBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|r
operator|.
name|read
argument_list|(
name|c
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|bodyBuf
operator|.
name|append
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|date
operator|==
literal|null
operator|&&
name|props
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|date
operator|=
name|dateFormat
operator|.
name|parse
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// do not fail test just because a date could not be parsed
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ignoring date parse exception (assigning 'now') for: "
operator|+
name|props
operator|.
name|getProperty
argument_list|(
literal|"date"
argument_list|)
argument_list|)
expr_stmt|;
name|date
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
comment|// now
block|}
block|}
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|bodyBuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setProps
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
block|}
end_class

end_unit

