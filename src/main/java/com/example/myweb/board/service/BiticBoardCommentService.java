package com.example.myweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.myweb.board.dto.BiticBoardCommentDTO;
import com.example.myweb.board.entity.BiticBoardCommentEntity;
import com.example.myweb.board.entity.BiticBoardEntity;
import com.example.myweb.board.repository.BiticBoardCommentRepository;
import com.example.myweb.board.repository.BiticBoardRepository;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BiticBoardCommentService {
	private final BiticBoardCommentRepository biticBoardCommentRepository;
	private final BiticBoardRepository biticBoardRepository;
	private final UserRepository userRepository;
	private final HttpSession session;

	
	public Long save(BiticBoardCommentDTO biticBoardCommentDTO) {
        // 세션에서 loginid와 nickname 가져오기
        String loginid = (String) session.getAttribute("loginid");
        String nickname = (String) session.getAttribute("nickname");

        if (loginid == null || nickname == null) {
            return null; // 로그인 정보가 없으면 null 반환
        }

        Optional<UserEntity> optionalUserEntity = userRepository.findByLoginidAndNickname(loginid, nickname);
        if (!optionalUserEntity.isPresent()) {
            return null; // 사용자가 존재하지 않으면 null 반환
        }

        UserEntity userEntity = optionalUserEntity.get();
        Optional<BiticBoardEntity> optionalBiticBoardEntity = biticBoardRepository.findById(biticBoardCommentDTO.getBiticBoardSeq());
        if (optionalBiticBoardEntity.isPresent()) {
            BiticBoardEntity biticBoardEntity = optionalBiticBoardEntity.get();
            BiticBoardCommentEntity biticBoardCommentEntity = BiticBoardCommentEntity.toSaveEntity(biticBoardCommentDTO, biticBoardEntity, userEntity);
            return biticBoardCommentRepository.save(biticBoardCommentEntity).getSeq();
        } else {
            return null;
        }
    }

	@Transactional
	public List<BiticBoardCommentDTO> findAll(Long biticBoardSeq) {
		// select * from bitic_board_comment_table where bitic_board_seq=? order by id desc;
		BiticBoardEntity biticBoardEntity = biticBoardRepository.findById(biticBoardSeq).get();
		List<BiticBoardCommentEntity> biticBoardCommentEntitiyList = biticBoardCommentRepository.findAllByBiticBoardEntityOrderBySeqDesc(biticBoardEntity);
		
		// EntityList => DTOList
		List<BiticBoardCommentDTO> biticBoardCommentDTOList = new ArrayList<>();
		for(BiticBoardCommentEntity biticBoardCommentEntity : biticBoardCommentEntitiyList) {
			BiticBoardCommentDTO biticBoardCommentDTO = BiticBoardCommentDTO.toBiticBoardCommentDTO(biticBoardCommentEntity);
			biticBoardCommentDTOList.add(biticBoardCommentDTO);
		}
		
		return biticBoardCommentDTOList;
	}



}