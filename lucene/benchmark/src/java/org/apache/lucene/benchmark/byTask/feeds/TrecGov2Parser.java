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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Parser for the GOV2 collection format  */
end_comment

begin_class
DECL|class|TrecGov2Parser
specifier|public
class|class
name|TrecGov2Parser
extends|extends
name|TrecDocParser
block|{
DECL|field|DATE
specifier|private
specifier|static
specifier|final
name|String
name|DATE
init|=
literal|"Date: "
decl_stmt|;
DECL|field|DATE_END
specifier|private
specifier|static
specifier|final
name|String
name|DATE_END
init|=
name|TrecContentSource
operator|.
name|NEW_LINE
decl_stmt|;
DECL|field|DOCHDR
specifier|private
specifier|static
specifier|final
name|String
name|DOCHDR
init|=
literal|"<DOCHDR>"
decl_stmt|;
DECL|field|TERMINATING_DOCHDR
specifier|private
specifier|static
specifier|final
name|String
name|TERMINATING_DOCHDR
init|=
literal|"</DOCHDR>"
decl_stmt|;
DECL|field|TERMINATING_DOCHDR_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|TERMINATING_DOCHDR_LENGTH
init|=
name|TERMINATING_DOCHDR
operator|.
name|length
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
name|TrecContentSource
name|trecSrc
parameter_list|,
name|StringBuilder
name|docBuf
parameter_list|,
name|ParsePathType
name|pathType
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Set up a (per-thread) reused Reader over the read content, reset it to re-read from docBuf
name|Reader
name|r
init|=
name|trecSrc
operator|.
name|getTrecDocReader
argument_list|(
name|docBuf
argument_list|)
decl_stmt|;
comment|// skip some of the text, optionally set date
name|Date
name|date
init|=
literal|null
decl_stmt|;
name|int
name|h1
init|=
name|docBuf
operator|.
name|indexOf
argument_list|(
name|DOCHDR
argument_list|)
decl_stmt|;
if|if
condition|(
name|h1
operator|>=
literal|0
condition|)
block|{
name|int
name|h2
init|=
name|docBuf
operator|.
name|indexOf
argument_list|(
name|TERMINATING_DOCHDR
argument_list|,
name|h1
argument_list|)
decl_stmt|;
name|String
name|dateStr
init|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|DATE
argument_list|,
name|DATE_END
argument_list|,
name|h2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateStr
operator|!=
literal|null
condition|)
block|{
name|date
operator|=
name|trecSrc
operator|.
name|parseDate
argument_list|(
name|dateStr
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|mark
argument_list|(
name|h2
operator|+
name|TERMINATING_DOCHDR_LENGTH
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|reset
argument_list|()
expr_stmt|;
name|HTMLParser
name|htmlParser
init|=
name|trecSrc
operator|.
name|getHtmlParser
argument_list|()
decl_stmt|;
return|return
name|htmlParser
operator|.
name|parse
argument_list|(
name|docData
argument_list|,
name|name
argument_list|,
name|date
argument_list|,
literal|null
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

