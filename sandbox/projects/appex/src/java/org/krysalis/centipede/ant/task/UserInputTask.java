begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*****************************************************************************  * Copyright (C) The Apache Software Foundation. All rights reserved.        *  * ------------------------------------------------------------------------- *  * This software is published under the terms of the Apache Software License *  * version 1.1, a copy of which has been included  with this distribution in *  * the LICENSE file.                                                         *  *****************************************************************************/
end_comment

begin_package
DECL|package|org.krysalis.centipede.ant.task
package|package
name|org
operator|.
name|krysalis
operator|.
name|centipede
operator|.
name|ant
operator|.
name|task
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|Property
import|;
end_import

begin_comment
comment|/**  * Task to ask property values to the user. Uses current value as default.  *  * @author<a href="mailto:barozzi@nicolaken.com">Nicola Ken Barozzi</a>  * @created 14 January 2002  * @version CVS $Revision$ $Date$  */
end_comment

begin_class
DECL|class|UserInputTask
specifier|public
class|class
name|UserInputTask
extends|extends
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
block|{
DECL|field|question
specifier|private
name|String
name|question
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
comment|/**    * Constructor.    */
DECL|method|UserInputTask
specifier|public
name|UserInputTask
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initializes the task.    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|question
operator|=
literal|"?"
expr_stmt|;
block|}
comment|/**    * Run the task.    * @exception org.apache.tools.ant.BuildException The exception raised during task execution.    */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
block|{
name|value
operator|=
name|project
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|String
name|defaultvalue
init|=
name|value
decl_stmt|;
comment|//if the property exists
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
operator|+
name|question
operator|+
literal|" ["
operator|+
name|value
operator|+
literal|"] "
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|value
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|value
operator|=
name|defaultvalue
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|project
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|project
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|defaultvalue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Sets the prompt text that will be presented to the user.    * @param prompt String    */
DECL|method|addText
specifier|public
name|void
name|addText
parameter_list|(
name|String
name|question
parameter_list|)
block|{
name|this
operator|.
name|question
operator|=
name|question
expr_stmt|;
block|}
DECL|method|setQuestion
specifier|public
name|void
name|setQuestion
parameter_list|(
name|String
name|question
parameter_list|)
block|{
name|this
operator|.
name|question
operator|=
name|question
expr_stmt|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

