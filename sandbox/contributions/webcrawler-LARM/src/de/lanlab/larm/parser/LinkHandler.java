begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.parser
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|parser
package|;
end_package

begin_interface
DECL|interface|LinkHandler
specifier|public
interface|interface
name|LinkHandler
block|{
DECL|method|handleLink
specifier|public
name|void
name|handleLink
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|isFrame
parameter_list|)
function_decl|;
DECL|method|handleBase
specifier|public
name|void
name|handleBase
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
DECL|method|handleTitle
specifier|public
name|void
name|handleTitle
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

