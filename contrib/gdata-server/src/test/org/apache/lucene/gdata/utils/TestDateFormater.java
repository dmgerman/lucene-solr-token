begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

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
name|text
operator|.
name|SimpleDateFormat
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestDateFormater
specifier|public
class|class
name|TestDateFormater
extends|extends
name|TestCase
block|{
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
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
comment|/*      * Test method for 'org.apache.lucene.gdata.utils.DateFormater.formatDate(Date, String)'      */
DECL|method|testFormatDate
specifier|public
name|void
name|testFormatDate
parameter_list|()
throws|throws
name|ParseException
block|{
comment|// this reg. --> bit weak but does the job
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
name|pattern
init|=
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[A-Z][a-z]{1,2}, [0-9]{1,2} [A-Z][a-z]{2} [0-9]{4} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2} [A-Z]{2,4}"
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|String
name|formatedDate
init|=
name|DateFormater
operator|.
name|formatDate
argument_list|(
name|date
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|.
name|matcher
argument_list|(
name|formatedDate
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|DateFormater
operator|.
name|parseDate
argument_list|(
literal|"Sun, 25 Jun 2006 13:51:23 +0000"
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT_TIME_OFFSET
argument_list|)
expr_stmt|;
name|DateFormater
operator|.
name|parseDate
argument_list|(
literal|"Sun, 25 Jun 2006 13:51:23 CEST"
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT_TIME_OFFSET
argument_list|)
expr_stmt|;
comment|//TODO extend this
block|}
DECL|method|testFormatDateStack
specifier|public
name|void
name|testFormatDateStack
parameter_list|()
block|{
name|DateFormater
name|formater
init|=
operator|new
name|DateFormater
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|f1
init|=
name|formater
operator|.
name|getFormater
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|f2
init|=
name|formater
operator|.
name|getFormater
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|)
expr_stmt|;
name|formater
operator|.
name|returnFomater
argument_list|(
name|f1
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|f1
argument_list|,
name|formater
operator|.
name|getFormater
argument_list|()
argument_list|)
expr_stmt|;
name|formater
operator|.
name|returnFomater
argument_list|(
name|f2
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|f2
argument_list|,
name|formater
operator|.
name|getFormater
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

