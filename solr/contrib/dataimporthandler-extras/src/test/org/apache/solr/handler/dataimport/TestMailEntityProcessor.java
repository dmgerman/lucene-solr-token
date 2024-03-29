begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|Entity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|// Test mailbox is like this: foldername(mailcount)
end_comment

begin_comment
comment|// top1(2) -> child11(6)
end_comment

begin_comment
comment|//         -> child12(0)
end_comment

begin_comment
comment|// top2(2) -> child21(1)
end_comment

begin_comment
comment|//                 -> grandchild211(2)
end_comment

begin_comment
comment|//                 -> grandchild212(1)
end_comment

begin_comment
comment|//         -> child22(2)
end_comment

begin_comment
comment|/**  * Test for MailEntityProcessor. The tests are marked as ignored because we'd need a mail server (real or mocked) for  * these to work.  *  * TODO: Find a way to make the tests actually test code  *  *  * @see org.apache.solr.handler.dataimport.MailEntityProcessor  * @since solr 1.4  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|class|TestMailEntityProcessor
specifier|public
class|class
name|TestMailEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
comment|// Credentials
DECL|field|user
specifier|private
specifier|static
specifier|final
name|String
name|user
init|=
literal|"user"
decl_stmt|;
DECL|field|password
specifier|private
specifier|static
specifier|final
name|String
name|password
init|=
literal|"password"
decl_stmt|;
DECL|field|host
specifier|private
specifier|static
specifier|final
name|String
name|host
init|=
literal|"host"
decl_stmt|;
DECL|field|protocol
specifier|private
specifier|static
specifier|final
name|String
name|protocol
init|=
literal|"imaps"
decl_stmt|;
DECL|field|paramMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testConnection
specifier|public
name|void
name|testConnection
parameter_list|()
block|{
comment|// also tests recurse = false and default settings
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top2"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top1 did not return 2 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testRecursion
specifier|public
name|void
name|testRecursion
parameter_list|()
block|{
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top2"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top2 and its children did not return 8 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testExclude
specifier|public
name|void
name|testExclude
parameter_list|()
block|{
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top2"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"exclude"
argument_list|,
literal|".*grandchild.*"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top2 and its direct children did not return 5 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testInclude
specifier|public
name|void
name|testInclude
parameter_list|()
block|{
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top2"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"include"
argument_list|,
literal|".*grandchild.*"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top2 and its direct children did not return 3 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testIncludeAndExclude
specifier|public
name|void
name|testIncludeAndExclude
parameter_list|()
block|{
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top1,top2"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"exclude"
argument_list|,
literal|".*top1.*"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"include"
argument_list|,
literal|".*grandchild.*"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top2 and its direct children did not return 3 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Needs a Mock Mail Server to work"
argument_list|)
DECL|method|testFetchTimeSince
specifier|public
name|void
name|testFetchTimeSince
parameter_list|()
throws|throws
name|ParseException
block|{
name|paramMap
operator|.
name|put
argument_list|(
literal|"folders"
argument_list|,
literal|"top1/child11"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"recurse"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"processAttachement"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|paramMap
operator|.
name|put
argument_list|(
literal|"fetchMailsSince"
argument_list|,
literal|"2008-12-26 00:00:00"
argument_list|)
expr_stmt|;
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|getConfigFromMap
argument_list|(
name|paramMap
argument_list|)
argument_list|)
expr_stmt|;
name|Entity
name|ent
init|=
name|di
operator|.
name|getConfig
argument_list|()
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
literal|null
argument_list|,
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrWriterImpl
name|swi
init|=
operator|new
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|swi
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"top2 and its direct children did not return 3 messages"
argument_list|,
name|swi
operator|.
name|docs
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfigFromMap
specifier|private
name|String
name|getConfigFromMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|String
name|conf
init|=
literal|"<dataConfig>"
operator|+
literal|"<document>"
operator|+
literal|"<entity processor=\"org.apache.solr.handler.dataimport.MailEntityProcessor\" "
operator|+
literal|"someconfig"
operator|+
literal|"/>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"password"
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"host"
argument_list|,
name|host
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"protocol"
argument_list|,
name|protocol
argument_list|)
expr_stmt|;
name|StringBuilder
name|attribs
init|=
operator|new
name|StringBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|params
operator|.
name|keySet
argument_list|()
control|)
name|attribs
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
operator|+
literal|"\""
argument_list|)
operator|.
name|append
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
return|return
name|conf
operator|.
name|replace
argument_list|(
literal|"someconfig"
argument_list|,
name|attribs
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|class|SolrWriterImpl
specifier|static
class|class
name|SolrWriterImpl
extends|extends
name|SolrWriter
block|{
DECL|field|docs
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|deleteAllCalled
name|Boolean
name|deleteAllCalled
decl_stmt|;
DECL|field|commitCalled
name|Boolean
name|commitCalled
decl_stmt|;
DECL|method|SolrWriterImpl
specifier|public
name|SolrWriterImpl
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
return|return
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doDeleteAll
specifier|public
name|void
name|doDeleteAll
parameter_list|()
block|{
name|deleteAllCalled
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|commitCalled
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

