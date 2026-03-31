package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.domain.entity.mysql.MediaProduct;
import com.txwx.webchat.domain.vo.MediaProductVo;
import com.txwx.webchat.service.IMediaProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/mediaProduct")
@Tag(name = "02--【自媒体】--产品")
public class MediaProductsController extends BaseController {

    @Autowired
    private IMediaProductsService iMediaProductsService;

    @PostMapping("/add")
    @Operation(summary = "添加产品")
    public AjaxResult add(@RequestBody MediaProduct mediaProduct) {
        boolean res = iMediaProductsService.add(mediaProduct);
        if (res) {
            return success("保存成功");
        }else {
            return error("保存失败");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新产品")
    public AjaxResult update(@RequestBody MediaProduct mediaProduct) {
        if (mediaProduct.getId() == null || mediaProduct.getId() < 0){
            return error("请提供正确的产品id");
        }
        boolean res = iMediaProductsService.updateById(mediaProduct);
        if (res) {
            return success("更新成功");
        }else {
            return error("更新失败");
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除产品")
    public AjaxResult delete(@RequestBody List<Long> ids) {
        if (ids.isEmpty()) {
            return error("请提供正确的产品id");
        }
        boolean res = iMediaProductsService.delete(ids);
        if (res) {
            return success("成功删除 " + ids.size() + " 个产品");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/list")
    @Operation(summary = "产品列表")
    public AjaxResult selectList() {
        List<MediaProductVo> res = iMediaProductsService.selectList();
        return success(res);
    }

    @GetMapping("/getOne")
    @Operation(summary = "产品详情")
    public AjaxResult getOne(Long id) {
        MediaProductVo res = iMediaProductsService.selectById(id);
        return success(res);
    }
}
