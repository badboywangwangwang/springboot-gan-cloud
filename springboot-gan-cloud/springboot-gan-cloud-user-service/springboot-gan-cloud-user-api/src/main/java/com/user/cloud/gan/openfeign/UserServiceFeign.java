
package com.user.cloud.gan.openfeign;

import com.common.cloud.gan.dto.Result;
import com.user.cloud.gan.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "springboot-gan-cloud-user-service", path = "/users")
public interface UserServiceFeign {

    @GetMapping(value = "/admin/{token}")
    Result getAdminUserByToken(@PathVariable(value = "token") String token);

    @GetMapping(value = "/mall/getDetailByToken")
    Result<UserDTO> getMallUserByToken(@RequestParam(value = "token") String token);
}
