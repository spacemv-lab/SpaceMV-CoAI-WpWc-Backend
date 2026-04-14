package com.txwx.social.crm.domain;

import com.txwx.social.api.domain.dto.ProductChannelDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.api.domain.dto.UserPermissionDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProductAddOrUpdateRequest {

    private SimpleProductDTO productDTO;

    private List<UserPermissionDTO> userPermissions;

    private List<ProductChannelDTO> productChannels;
}
