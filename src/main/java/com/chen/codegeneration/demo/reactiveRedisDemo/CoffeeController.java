package com.chen.codegeneration.demo.reactiveRedisDemo;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.BeanUtils.beanToMap;

@Log4j2
@RestController
public class CoffeeController {

	@Setter(onMethod_ = @Autowired)
	private ReactiveRedisOperations<String, Object> redisOps;

	@GetMapping("/coffees")
	public Flux<Object> all() {
		return redisOps.keys("*")
				.flatMap(redisOps.opsForValue()::get);
	}


	/**
	 * ReactiveRedisTemplate支持Redis字符串，散列，列表，集合，有序集合等基本的数据类型。
	 *
	 * @return
	 */
    @GetMapping("/save")
	public Mono<Boolean> save() {
		User user = new User().setId(1L).setName("testUser").setRights(new ArrayList<>() {{
			add(new Rights().setUserId(1L).setId(11L).setName("testRight"));
		}});
		ReactiveHashOperations<String, String, Object> opsForHash = redisOps.opsForHash();
		Mono<Boolean> userRs = opsForHash.putAll("user:" + user.getId(), beanToMap(user));
		if (user.getRights() != null) {
			ReactiveListOperations<String, Object> opsForRights = redisOps.opsForList();
			opsForRights.leftPushAll("user:rights:" + user.getId(), user.getRights())
					.subscribe(l -> log.info("add rights:{}", l));
		}
		return userRs;
	}

	/**
	 * Redis HyperLogLog结构可以统计一个集合内不同元素的数量。
	 * 使用HyperLogLog统计每天登录的用户量
	 *
	 * @param user
	 * @return
	 */
	public Mono<Long> login() {
        Long userId = 1L;
		ReactiveHyperLogLogOperations<String, Object> opsForHyperLogLog = redisOps.opsForHyperLogLog();
		return opsForHyperLogLog.add("user:login:number:" + LocalDateTime.now().toString().substring(0, 10), userId);
	}


	/**
	 * Redis BitMap（位图）通过一个Bit位表示某个元素对应的值或者状态。由于Bit是计算机存储中最小的单位，使用它进行储存将非常节省空间。
	 * 使用BitMap记录用户本周是否有签到
	 *
	 * @param userId
	 */
	public void addSignInFlag() {
        long userId = 1L;
		String key = "user:signIn:" + LocalDateTime.now().getDayOfYear() / 7 + (userId >> 16);
		redisOps.opsForValue().setBit(key, userId & 0xffff, true)
				.subscribe(b -> log.info("set:{},result:{}", key, b));
		// userId高48位用于将用户划分到不同的key，低16位作为位图偏移参数offset。
		// offset参数必须大于或等于0，小于2^32(bit 映射被限制在 512 MB 之内)。
	}


	/**
	 * Redis Geo可以存储地理位置信息，并对地理位置进行计算。
	 * 如查找给定范围内的仓库信息
	 *
	 * @param u
	 * @param dist
	 * @return
	 */
	public Flux<?> getWarehouseInDist(User u, double dist) {
		ReactiveGeoOperations<String, Object> geo = redisOps.opsForGeo();
		Circle circle = new Circle(new Point(u.getDeliveryAddressLon(), u.getDeliveryAddressLat()), dist);
		RedisGeoCommands.GeoRadiusCommandArgs args =
				RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending();
		return geo.radius("warehouse:address", circle, args);
		// warehouse:address这个集合中需要先保存好仓库地理位置信息。
		// ReactiveGeoOperations#radius方法可以查找集合中地理位置在给定范围内的元素，它中还支持添加元素到集合，计算集合中两个元素地理位置距离等操作。
	}

	/**
	 * ReactiveRedisTemplate也可以执行Lua脚本。
	 * 下面通过Lua脚本完成用户签到逻辑：如果用户今天未签到，允许签到，积分加1，如果用户今天已签到，则拒接操作。
	 */
	public Flux<String> addScore(long userId) {
		DefaultRedisScript<String> script = new DefaultRedisScript<>();
		script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/signIn.lua")));
		List<String> keys = new ArrayList<>();
		keys.add(String.valueOf(userId));
		keys.add(LocalDateTime.now().toString().substring(0, 10));
		return redisOps.execute(script, keys);
		// signIn.lua内容如下
		// local score=redis.call('hget','user:'..KEYS[1],'score')
		// local day=redis.call('hget','user:'..KEYS[1],'lastSignInDay')
		// if(day==KEYS[2])
		//     then
		//     return '0'
		// else
		//     redis.call('hset','user:'..KEYS[1],'score', score+1,'lastSignInDay',KEYS[2])
		//     return '1'
		// end
	}
}
