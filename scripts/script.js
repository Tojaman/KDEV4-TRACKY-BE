import http from 'k6/http';
import { check } from 'k6';
import { requestBody } from './cycleRequest.js';

export const options = {
    // constant-vus: 지정한 VU(가상 사용자)를 동시에 유지
    scenarios: {
        load_test: {
        executor: 'constant-vus',
        vus: 1000,           // 동시 1000명
        duration: '3s',     // 30초 동안 실행
        },
    }
    // thresholds: {
    //     // 실패율이 1% 초과 시 에러로 간주
    //     'http_req_failed': ['rate<0.01'],
    //     // 95% 요청 응답 시간이 500ms 이하
    //     'http_req_duration': ['p(95)<500'],
    // }, 
};

export default function () {
    const url = 'http://host.docker.internal:8082/hub/car/cycle';
    const server_url = 'https://api.tracky.kr:777/hub/car/cycle';
    const payload = JSON.stringify(requestBody);  // 직렬화
    const headers = {
        'Content-Type': 'application/json',
    };

    // 요청을 보낼 URL
    const res = http.post(url, payload, { headers });
    // HTTP 200 여부 체크
    check(res, { 'status is 200': (r) => r.status === 200 });
}
