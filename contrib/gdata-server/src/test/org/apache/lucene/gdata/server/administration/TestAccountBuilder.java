begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.administration
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|administration
package|;
end_package

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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
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
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
operator|.
name|AccountRole
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
DECL|class|TestAccountBuilder
specifier|public
class|class
name|TestAccountBuilder
extends|extends
name|TestCase
block|{
DECL|field|reader
specifier|private
name|StringReader
name|reader
decl_stmt|;
DECL|field|inputXML
specifier|private
name|String
name|inputXML
decl_stmt|;
DECL|field|invalidReader
specifier|private
name|StringReader
name|invalidReader
decl_stmt|;
DECL|field|invalidInputXML
specifier|private
name|String
name|invalidInputXML
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|inputXML
operator|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<account>"
operator|+
literal|"<account-name>simon</account-name>"
operator|+
literal|"<password>simon</password>"
operator|+
literal|"<account-role>6</account-role>"
operator|+
literal|"<account-owner>"
operator|+
literal|"<name>simon willnauer</name>"
operator|+
literal|"<email-address>simon@gmail.com</email-address>"
operator|+
literal|"<url>http://www.javawithchopsticks.de</url>"
operator|+
literal|"</account-owner>"
operator|+
literal|"</account>"
expr_stmt|;
name|this
operator|.
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|inputXML
argument_list|)
expr_stmt|;
name|this
operator|.
name|invalidInputXML
operator|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<account>"
operator|+
literal|"<account-name>simon</account-name>"
operator|+
literal|"<account-role>6</account-role>"
operator|+
literal|"<account-owner>"
operator|+
literal|"<name>simon willnauer</name>"
operator|+
literal|"<email-address>simon@gmail.com</email-address>"
operator|+
literal|"<url>http://www.javawithchopsticks.de</url>"
operator|+
literal|"</account-owner>"
operator|+
literal|"</account>"
expr_stmt|;
name|this
operator|.
name|invalidReader
operator|=
operator|new
name|StringReader
argument_list|(
name|this
operator|.
name|invalidInputXML
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.lucene.gdata.server.administration.AdminEntityBuilder.buildUser(Reader)'      */
DECL|method|testBuildUser
specifier|public
name|void
name|testBuildUser
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|GDataAccount
name|user
init|=
name|AccountBuilder
operator|.
name|buildAccount
argument_list|(
name|this
operator|.
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"simon"
argument_list|,
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simon willnauer"
argument_list|,
name|user
operator|.
name|getAuthorname
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simon@gmail.com"
argument_list|,
name|user
operator|.
name|getAuthorMail
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simon"
argument_list|,
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://www.javawithchopsticks.de"
argument_list|)
argument_list|,
name|user
operator|.
name|getAuthorLink
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|isUserInRole
argument_list|(
name|AccountRole
operator|.
name|ENTRYAMINISTRATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|user
operator|.
name|isUserInRole
argument_list|(
name|AccountRole
operator|.
name|FEEDAMINISTRATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|isUserInRole
argument_list|(
name|AccountRole
operator|.
name|USERADMINISTRATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuildUserWrongXML
specifier|public
name|void
name|testBuildUserWrongXML
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|AccountBuilder
operator|.
name|buildAccount
argument_list|(
name|this
operator|.
name|invalidReader
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"invalid xml"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{                      }
block|}
block|}
end_class

end_unit

