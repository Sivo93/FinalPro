package com.example.myweb.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.myweb.board.dto.FreeBoardCommentDTO;
import com.example.myweb.board.entity.FreeBoardCommentEntity;
import com.example.myweb.board.entity.FreeBoardEntity;
import com.example.myweb.board.repository.FreeBoardCommentRepository;
import com.example.myweb.board.repository.FreeBoardRepository;
import com.example.myweb.user.dto.UserDTO;
import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeBoardCommentService {
	private final FreeBoardCommentRepository freeBoardCommentRepository;
	private final FreeBoardRepository freeBoardRepository;
	private final UserRepository userRepository;
	private final HttpSession session;
	
//	public Long save(FreeBoardCommentDTO freeBoardCommentDTO) {
//		// 부모엔티티(freeBoardEntity)조회
//		Optional<FreeBoardEntity> optionalFreeBoardEntity = freeBoardRepository.findById(freeBoardCommentDTO.getFreeBoardSeq());
//		Optional<UserEntity> optionalUserEntity = userRepository.findByLoginidAndNickname(freeBoardCommentDTO.getLoginid(), freeBoardCommentDTO.getNickname());
//		if(optionalFreeBoardEntity.isPresent() && optionalUserEntity.isPresent()) {
//			FreeBoardEntity freeBoardEntity = optionalFreeBoardEntity.get();
//			UserEntity userEntity = optionalUserEntity.get();
//			FreeBoardCommentEntity freeBoardCommentEntity = FreeBoardCommentEntity.toSaveEntity(freeBoardCommentDTO, freeBoardEntity, userEntity );
//			
//			return freeBoardCommentRepository.save(freeBoardCommentEntity).getSeq();
//		} else {
//			return null;
//		}
//		
//	}
	
	public Long save(FreeBoardCommentDTO freeBoardCommentDTO) {
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
        Optional<FreeBoardEntity> optionalFreeBoardEntity = freeBoardRepository.findById(freeBoardCommentDTO.getFreeBoardSeq());
        if (optionalFreeBoardEntity.isPresent()) {
            FreeBoardEntity freeBoardEntity = optionalFreeBoardEntity.get();
            FreeBoardCommentEntity freeBoardCommentEntity = FreeBoardCommentEntity.toSaveEntity(freeBoardCommentDTO, freeBoardEntity, userEntity);
            return freeBoardCommentRepository.save(freeBoardCommentEntity).getSeq();
        } else {
            return null;
        }
    }

	@Transactional
	public List<FreeBoardCommentDTO> findAll(Long freeBoardSeq) {
		// select * from free_board_comment_table where free_board_seq=? order by id desc;
		FreeBoardEntity freeBoardEntity = freeBoardRepository.findById(freeBoardSeq).get();
		List<FreeBoardCommentEntity> freeBoardCommentEntitiyList = freeBoardCommentRepository.findAllByFreeBoardEntityOrderBySeqDesc(freeBoardEntity);
		
		// EntityList => DTOList
		List<FreeBoardCommentDTO> freeBoardCommentDTOList = new ArrayList<>();
		for(FreeBoardCommentEntity freeBoardCommentEntity : freeBoardCommentEntitiyList) {
			FreeBoardCommentDTO freeBoardCommentDTO = FreeBoardCommentDTO.toFreeBoardCommentDTO(freeBoardCommentEntity);
			freeBoardCommentDTOList.add(freeBoardCommentDTO);
		}
		
		return freeBoardCommentDTOList;
	}



}